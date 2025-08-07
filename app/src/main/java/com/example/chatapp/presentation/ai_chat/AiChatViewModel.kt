package com.example.chatapp.presentation.ai_chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.geminiUseCase.GetGeminiResponseUseCase
import com.example.chatapp.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AiChatViewModel(
    private val getGeminiResponseUseCase: GetGeminiResponseUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(AiChatState())
    val uiState = _uiState.asStateFlow()

    companion object {
        const val USER_ID = "user"
        const val GEMINI_ID = "gemini"
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
    }

    fun sendMessage() {
        val userMessageText = uiState.value.currentMessage
        if (userMessageText.isBlank()) return

        // Add user message to the list
        val userMessage = Message(
            senderId = USER_ID,
            text = userMessageText,
            timestamp = System.currentTimeMillis()
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMessage,
            currentMessage = "",
            isLoading = true
        )

        // Get response from Gemini
        viewModelScope.launch {
            getGeminiResponseUseCase(userMessageText).onSuccess { responseText ->
                val geminiMessage = Message(senderId = GEMINI_ID, text = responseText, timestamp = System.currentTimeMillis())
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + geminiMessage,
                    isLoading = false
                )
            }.onFailure {
                val errorMessage = Message(senderId = GEMINI_ID, text = "Sorry, something went wrong.", timestamp = System.currentTimeMillis())
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + errorMessage,
                    isLoading = false
                )
            }
        }
    }
}