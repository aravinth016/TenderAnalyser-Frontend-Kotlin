package com.simats.tenderanalyser

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class BidderSettingsViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun clearError() { errorMessage = null }

    fun changePassword(
        userId: String,
        currentPass: String,
        newPass: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val request = ChangePasswordRequest(userId, currentPass, newPass)
                val response = ApiClient.apiService.changeBidderPassword(request)

                if (response.success) {
                    onSuccess() // Trigger the navigation to the success screen!
                } else {
                    errorMessage = response.message ?: "Failed to update password."
                }
            } catch (e: Exception) {
                errorMessage = "Network error. Please check your connection."
            } finally {
                isLoading = false
            }
        }
    }
}