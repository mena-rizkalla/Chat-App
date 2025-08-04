package com.example.chatapp.presentation.globalChatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.GetGlobalMessagesUseCase
import com.example.chatapp.domain.chatUseCases.SendGlobalMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GlobalChatViewModel(
    private val getGlobalMessagesUseCase: GetGlobalMessagesUseCase,
    private val sendGlobalMessageUseCase: SendGlobalMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GlobalChatState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(currentUserId = getCurrentUserUseCase()?.uid ?: "")
        getGlobalMessagesUseCase()
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
                sendGlobalMessageUseCase(text)
                _uiState.value = _uiState.value.copy(currentMessage = "") // Clear input
            }
        }
    }
}