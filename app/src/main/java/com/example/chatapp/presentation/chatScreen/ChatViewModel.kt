package com.example.chatapp.presentation.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetTypingStatusUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import com.example.chatapp.domain.chatUseCases.TogglePrivateMessageReactionUseCase
import com.example.chatapp.domain.chatUseCases.UpdateTypingStatusUseCase
import com.example.chatapp.domain.model.Reaction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ChatViewModel(
    private val receiverId: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleReactionUseCase: TogglePrivateMessageReactionUseCase,
    private val getTypingStatusUseCase: GetTypingStatusUseCase,
    private val updateTypingStatusUseCase: UpdateTypingStatusUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState = _uiState.asStateFlow()

    private val textInputFlow = MutableStateFlow("")

    init {
        _uiState.value = _uiState.value.copy(currentUserId = getCurrentUserUseCase()?.uid ?: "")
        getChatMessagesUseCase(receiverId)
            .onEach { messages ->
                _uiState.value = _uiState.value.copy(messages = messages)
            }.launchIn(viewModelScope)

        getTypingStatusUseCase(receiverId)
            .onEach { isTyping ->
                _uiState.value = _uiState.value.copy(isOtherUserTyping = isTyping)
            }.launchIn(viewModelScope)

        viewModelScope.launch {
            textInputFlow
                .onEach { text ->
                    if (text.isNotBlank()) {
                        updateTypingStatusUseCase(receiverId, true)
                    }
                }
                .debounce(2000) // After 2s of no new text...
                .collect {
                    // ...update status to not typing.
                    updateTypingStatusUseCase(receiverId, false)
                }
        }
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
        textInputFlow.value = message
    }

    fun sendMessage() {
        viewModelScope.launch {
            val text = uiState.value.currentMessage
            if (text.isNotBlank()) {
                sendMessageUseCase(receiverId, text)
                _uiState.value = _uiState.value.copy(currentMessage = "") // Clear input
                textInputFlow.value = ""
                updateTypingStatusUseCase(receiverId, false)
            }
        }
    }

    fun toggleReaction(messageId: String, reaction: Reaction) {
        viewModelScope.launch {
            toggleReactionUseCase(
                receiverId = receiverId,
                messageId = messageId,
                reaction = reaction
            )
            // No need to update state here, Firestore's snapshot listener will do it for us.
        }
    }
}