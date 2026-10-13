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
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

class AuthorityViewModel : ViewModel() {
    // Step 1: Admin Info
    var adminName = ""
    var designation = ""
    var email = ""
    var password = ""

    // Step 2: Department Details
    var deptName = ""
    var officeCode = ""
    var sector = "Select Sector"
    var hqAddress = ""

    // Step 3: Verification
    var signatoryName = ""
    var contactNumber = ""

    private val _registrationState = MutableStateFlow<Result<String>?>(null)
    val registrationState: StateFlow<Result<String>?> = _registrationState

    fun resetState() { _registrationState.value = null }

    fun submitRegistration(context: Context, authLetter: Uri, idProof: Uri, dscKey: Uri, deptReg: Uri) {
        viewModelScope.launch {
            try {
                // 1. Convert Strings to RequestBody for the backend
                val adminNameBody = adminName.toRequestBody("text/plain".toMediaTypeOrNull())
                val designationBody = designation.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
                val deptNameBody = deptName.toRequestBody("text/plain".toMediaTypeOrNull())
                val officeCodeBody = officeCode.toRequestBody("text/plain".toMediaTypeOrNull())
                val sectorBody = sector.toRequestBody("text/plain".toMediaTypeOrNull())
                val hqAddressBody = hqAddress.toRequestBody("text/plain".toMediaTypeOrNull())
                val signatoryNameBody = signatoryName.toRequestBody("text/plain".toMediaTypeOrNull())
                val contactNumberBody = contactNumber.toRequestBody("text/plain".toMediaTypeOrNull())

                // 2. Convert Android Uris into actual File objects
                val authLetterPart = prepareFilePart(context, "authLetter", authLetter)
                val idProofPart = prepareFilePart(context, "idProof", idProof)
                val dscKeyPart = prepareFilePart(context, "dscKey", dscKey)
                val deptRegPart = prepareFilePart(context, "deptReg", deptReg)

                if (authLetterPart == null || idProofPart == null || dscKeyPart == null || deptRegPart == null) {
                    _registrationState.value = Result.failure(Exception("Could not process PDF files."))
                    return@launch
                }

                // 3. 🚨 MAKE THE ACTUAL API CALL TO PYTHON!
                val response = ApiClient.apiService.registerAuthority(
                    adminName = adminNameBody,
                    designation = designationBody,
                    email = emailBody,
                    password = passwordBody,
                    deptName = deptNameBody,
                    officeCode = officeCodeBody,
                    sector = sectorBody,
                    hqAddress = hqAddressBody,
                    signatoryName = signatoryNameBody,
                    contactNumber = contactNumberBody,
                    authLetter = authLetterPart,
                    idProof = idProofPart,
                    dscKey = dscKeyPart,
                    deptReg = deptRegPart
                )

                _registrationState.value = Result.success(response.message ?: "Authority Registration Submitted!")

            } catch (e: HttpException) {
                // Catches errors thrown by Python (e.g. Email already exists)
                val errorString = e.response()?.errorBody()?.string() ?: "Server rejected the request"
                _registrationState.value = Result.failure(Exception("Error: $errorString"))
            } catch (e: Exception) {
                // Catches network/connection errors
                _registrationState.value = Result.failure(Exception("Network error: ${e.message}"))
            }
        }
    }

    // Helper function to safely copy the PDF into a temporary file for upload
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