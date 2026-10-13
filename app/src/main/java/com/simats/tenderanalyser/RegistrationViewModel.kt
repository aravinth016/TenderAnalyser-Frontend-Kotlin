package com.simats.tenderanalyser

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException // 🚨 CRITICAL IMPORT ADDED HERE
import java.io.File
import java.io.FileOutputStream

class RegistrationViewModel : ViewModel() {
    // 1. Hold the text data from Step 1
    var fullName = ""
    var email = ""
    var password = ""
    var companyName = ""
    var gstin = ""

    // 2. Track the success/error state to show to the user
    private val _registrationState = MutableStateFlow<Result<String>?>(null)
    val registrationState: StateFlow<Result<String>?> = _registrationState

    fun resetState() {
        _registrationState.value = null
    }

    // 3. The function that actually talks to the Server
    fun registerUser(context: Context, gstUri: Uri, incUri: Uri, panUri: Uri) {
        viewModelScope.launch {
            try {
                // Convert strings to RequestBody (Required for @Multipart)
                val fNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val passBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
                val compBody = companyName.toRequestBody("text/plain".toMediaTypeOrNull())
                val gstBody = gstin.toRequestBody("text/plain".toMediaTypeOrNull())

                // Convert Android Uris into actual File objects
                val gstPart = prepareFilePart(context, "gst_certificate", gstUri)
                val incPart = prepareFilePart(context, "incorporation_certificate", incUri)
                val panPart = prepareFilePart(context, "pan_card", panUri)

                if (gstPart == null || incPart == null || panPart == null) {
                    _registrationState.value = Result.failure(Exception("Could not process PDF files."))
                    return@launch
                }

                // Call the API
                val response = ApiClient.apiService.registerUser(
                    fullName = fNameBody,
                    email = emailBody,
                    password = passBody,
                    companyName = compBody,
                    gstin = gstBody,
                    gst_certificate = gstPart,
                    incorporation_certificate = incPart,
                    pan_card = panPart
                )

                // 🚨 FIXED: If code reaches here, Retrofit guarantees it was a 2xx Success!
                // Python doesn't send "success: true", so we don't check for it anymore.
                _registrationState.value = Result.success(response.message ?: "Registration Successful")

            } catch (e: HttpException) {
                // 🚨 FIXED: The server WAS reached, but Python rejected the data (e.g. Email exists)
                // Let's grab the actual error message Python sent!
                val errorString = e.response()?.errorBody()?.string() ?: "Server rejected the request"
                _registrationState.value = Result.failure(Exception("Python Error: $errorString"))

            } catch (e: Exception) {
                // 🚨 This only triggers if the Wi-Fi is disconnected or Mac is turned off
                _registrationState.value = Result.failure(Exception("Network error. Server actually unreachable."))
            }
        }
    }

    // Helper function to copy the PDF into a temporary file for upload
    private fun prepareFilePart(context: Context, partName: String, fileUri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(fileUri) ?: return null

            // Create a temporary file in the app's cache directory
            val tempFile = File(context.cacheDir, "${partName}_${System.currentTimeMillis()}.pdf")
            val outputStream = FileOutputStream(tempFile)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, tempFile.name, requestFile)
        } catch (e: Exception) {
            null
        }
    }
}