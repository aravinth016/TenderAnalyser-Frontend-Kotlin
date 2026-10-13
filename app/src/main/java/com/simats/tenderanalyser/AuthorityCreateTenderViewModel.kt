package com.simats.tenderanalyser

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class AuthorityCreateTenderViewModel : ViewModel() {

    // Step 1 Data
    var projectTitle by mutableStateOf("")
    var nitNumber by mutableStateOf("")
    var projectDescription by mutableStateOf("")

    // Step 2 Data
    var estimatedValue by mutableStateOf("")
    var emdAmount by mutableStateOf("")
    var sector by mutableStateOf("Select Sector")
    var location by mutableStateOf("")
    var deadline by mutableStateOf("DD/MM/YYYY")

    // Step 3 Data (The Magnificent Seven Checklist)
    var reqGstPan by mutableStateOf(true)
    var reqAffidavit by mutableStateOf(true)
    var reqEmdReceipt by mutableStateOf(true)
    var reqPastWork by mutableStateOf(true)
    var reqFinSolvency by mutableStateOf(true)
    var reqTechProposal by mutableStateOf(true)
    var reqBoq by mutableStateOf(true) // Always true

    var tenderDocumentUris by mutableStateOf<List<Uri>>(emptyList())

    // UI States
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _publishState = MutableStateFlow(false)
    val publishState: StateFlow<Boolean> = _publishState

    private val _errorFlow = MutableSharedFlow<String>()
    val errorFlow = _errorFlow.asSharedFlow()

    fun publishTender(context: Context) {
        // 1. Basic Validation
        if (projectTitle.isBlank() || nitNumber.isBlank() || estimatedValue.isBlank()) {
            viewModelScope.launch { _errorFlow.emit("Please fill in all mandatory fields.") }
            return
        }

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // 2. Convert text fields to RequestBody (Required for Multipart API calls)
                fun createPart(text: String) = text.toRequestBody("text/plain".toMediaTypeOrNull())

                // 3. Process Multiple PDFs
                val fileParts = tenderDocumentUris.mapNotNull { uri ->
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()

                    if (bytes != null) {
                        val requestFile = bytes.toRequestBody("application/pdf".toMediaTypeOrNull())
                        val fileName = uri.lastPathSegment?.substringAfterLast("/") ?: "document.pdf"
                        // 🚨 "tenderDocuments" must match the Python API exact spelling
                        MultipartBody.Part.createFormData("tenderDocuments", fileName, requestFile)
                    } else null
                }

                // 4. Send API Call
                val response = ApiClient.apiService.createTender(
                    projectTitle = createPart(projectTitle),
                    nitNumber = createPart(nitNumber),
                    projectDescription = createPart(projectDescription),
                    estimatedValue = createPart(estimatedValue),
                    emdAmount = createPart(emdAmount),
                    sector = createPart(sector),
                    location = createPart(location),
                    deadline = createPart(deadline),

                    // The 7 New Professional Requirements
                    reqGstPan = createPart(reqGstPan.toString()),
                    reqAffidavit = createPart(reqAffidavit.toString()),
                    reqEmdReceipt = createPart(reqEmdReceipt.toString()),
                    reqPastWork = createPart(reqPastWork.toString()),
                    reqFinSolvency = createPart(reqFinSolvency.toString()),
                    reqTechProposal = createPart(reqTechProposal.toString()),
                    reqBoq = createPart(reqBoq.toString()),

                    tenderDocuments = if (fileParts.isEmpty()) null else fileParts
                )

                // 5. Handle Response
                if (response.success) {
                    _publishState.value = true
                } else {
                    _errorFlow.emit(response.message ?: "Failed to publish tender")
                }

            } catch (e: Exception) {
                _errorFlow.emit("Network error: Could not reach server.")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun resetState() {
        _publishState.value = false
        projectTitle = ""
        nitNumber = ""
        projectDescription = ""
        estimatedValue = ""
        emdAmount = ""
        sector = "Select Sector"
        location = ""
        deadline = "DD/MM/YYYY"
        tenderDocumentUris = emptyList()

        reqGstPan = true
        reqAffidavit = true
        reqEmdReceipt = true
        reqPastWork = true
        reqFinSolvency = true
        reqTechProposal = true
        reqBoq = true
    }
}