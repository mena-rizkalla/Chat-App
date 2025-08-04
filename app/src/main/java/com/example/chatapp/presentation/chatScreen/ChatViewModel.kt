package com.example.chatapp.presentation.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ChatViewModel(
    private val receiverId: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(currentUserId = getCurrentUserUseCase()?.uid ?: "")
        getChatMessagesUseCase(receiverId)
            .onEach { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }.launchIn(viewModelScope)
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
    }

    fun sendMessage() {
        viewModelScope.launch {
            val text = uiState.value.currentMessage
            if (text.isNotBlank()) {
                sendMessageUseCase(receiverId, text)
                _uiState.value = _uiState.value.copy(currentMessage = "") // Clear input
            }
        }
    }
}