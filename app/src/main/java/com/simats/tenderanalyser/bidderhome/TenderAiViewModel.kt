package com.simats.tenderanalyser.bidderhome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.simats.tenderanalyser.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ChatMessage(val text: String, val isUser: Boolean)

class TenderAiViewModel : ViewModel() {

    private val _chatHistory = MutableStateFlow(
        listOf(ChatMessage("Welcome! What can I do for you?", isUser = false))
    )
    val chatHistory: StateFlow<List<ChatMessage>> = _chatHistory

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentTenderInfo: String = "No specific tender selected yet. " +
            "Please act as a helpful assistant for the Tender Analyser app. " +
            "If the user says 'hi' or starts a conversation, respond with: " +
            "'Hello! How can I help you with your tenders today?'"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = BuildConfig.GEMINI_API_KEY
    )

    fun setTenderContext(tenderId: String, details: String) {
        currentTenderInfo = "TENDER ID: $tenderId\nDETAILS: $details"
        _chatHistory.value = listOf(ChatMessage("Welcome! What can I do for you?", isUser = false))
    }

    fun askQuestion(userInput: String) {
        if (userInput.isBlank()) return

        viewModelScope.launch {
            val currentList = _chatHistory.value.toMutableList()
            currentList.add(ChatMessage(userInput, isUser = true))
            _chatHistory.value = currentList

            _isLoading.value = true
            try {
                // 🛑 STRICT GUARDRAILS & APP FEATURE INSTRUCTIONS
                val combinedPrompt = """
                    SYSTEM INSTRUCTION & GUARDRAILS:
                    You are an official AI Assistant for the "Tender Analyser" platform.
                    
                    PERMITTED TOPICS (You MUST answer these helpfully):
                    1. Government or private tenders, bidding rules, EMD, document requirements, deadlines, eligibility, and project scopes.
                    2. How the Tender Analyser app works, platform workflows, screen navigation, and feature guidance (e.g., how to view "My Bids", how to submit or delete a bid, how to use the bottom navigation, how to access profile/settings, and how to browse tenders).
                    3. Simple greetings (like "hi" or "hello") should be met with a friendly greeting.
                    
                    STRICT BOUNDARIES & REFUSAL:
                    If the user asks about ANY topic completely unrelated to tenders or the Tender Analyser app (such as general programming/coding, math problems, weather, recipes, movies, or general knowledge), you MUST strictly and swiftly decline by responding with:
                       "I'm sorry, but that question is outside my boundaries. I am your Tender Analyser assistant, focused only on tenders, bidding guidelines, and platform features."

                    CURRENT CONTEXT:
                    $currentTenderInfo
                    
                    USER QUESTION: $userInput
                """.trimIndent()

                val response = generativeModel.generateContent(combinedPrompt)
                val aiText = response.text ?: "I'm sorry, I couldn't process that request."

                val updatedList = _chatHistory.value.toMutableList()
                updatedList.add(ChatMessage(aiText, isUser = false))
                _chatHistory.value = updatedList
            } catch (e: Exception) {
                val errorList = _chatHistory.value.toMutableList()
                errorList.add(ChatMessage("Sorry, I'm having trouble connecting right now.", isUser = false))
                _chatHistory.value = errorList
            } finally {
                _isLoading.value = false
            }
        }
    }
}