package com.example.chatapp.presentation.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetLastSeenUseCase
import com.example.chatapp.domain.chatUseCases.GetTypingStatusUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import com.example.chatapp.domain.chatUseCases.TogglePrivateMessageReactionUseCase
import com.example.chatapp.domain.chatUseCases.UpdateLastSeenUseCase
import com.example.chatapp.domain.chatUseCases.UpdateTypingStatusUseCase
import com.example.chatapp.domain.geminiUseCase.GetGeminiResponseUseCase
import com.example.chatapp.domain.model.Reaction
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class ChatViewModel(
    private val receiverId: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleReactionUseCase: TogglePrivateMessageReactionUseCase,
    private val getTypingStatusUseCase: GetTypingStatusUseCase,
    private val updateTypingStatusUseCase: UpdateTypingStatusUseCase,
    private val getGeminiResponseUseCase: GetGeminiResponseUseCase,
    private val updateLastSeenUseCase: UpdateLastSeenUseCase,
    private val getLastSeenUseCase: GetLastSeenUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState = _uiState.asStateFlow()

    private val textInputFlow = MutableStateFlow("")
    init {
        // Set the current user's ID in the state
        _uiState.update { it.copy(currentUserId = getCurrentUserUseCase()?.uid ?: "") }

        // 1. Immediately update our own "last seen" timestamp in Firestore.
        // This signals to the other user that we are currently in the chat.
        viewModelScope.launch {
            updateLastSeenUseCase(receiverId)
        }

        // 2. Listen for the OTHER user's "last seen" timestamp in real-time.
        // The UI will use this to determine if our messages have been read.
        getLastSeenUseCase(receiverId)
            .onEach { lastSeenTimestamp ->
                _uiState.update { it.copy(receiverLastSeenTimestamp = lastSeenTimestamp) }
            }.launchIn(viewModelScope)


        // Fetch all chat messages and update the UI
        getChatMessagesUseCase(receiverId)
            .onEach { messagesFromDb ->
                _uiState.update { it.copy(messages = messagesFromDb) }
            }.launchIn(viewModelScope)

        // Listen for the other user's typing status
        getTypingStatusUseCase(receiverId)
            .onEach { isTyping ->
                _uiState.update { it.copy(isOtherUserTyping = isTyping) }
            }.launchIn(viewModelScope)

        // Set up the logic for sending our own typing status
        viewModelScope.launch {
            textInputFlow
                .onEach { text ->
                    // Signal that we are typing as soon as there's text
                    if (text.isNotBlank()) {
                        updateTypingStatusUseCase(receiverId, true)
                    }
                }
                // Wait for a 2-second pause in typing before sending "not typing"
                .debounce(2000)
                .collect {
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

    fun generateReplySuggestions() {
        val lastMessage = uiState.value.messages.lastOrNull { it.senderId != uiState.value.currentUserId }
        if (lastMessage == null || uiState.value.isGeneratingSuggestions) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingSuggestions = true, suggestedReplies = emptyList())

            val prompt = "Based on the message: \"${lastMessage.text}\", suggest three short, distinct, and natural-sounding replies. Format them as a simple numbered list without any extra text."
            getGeminiResponseUseCase(prompt).onSuccess { response ->
                val replies = response.lines().mapNotNull {
                    it.replaceFirst(Regex("^\\d+\\.?\\s*"), "").trim()
                }.filter { it.isNotBlank() }
                _uiState.value = _uiState.value.copy(suggestedReplies = replies, isGeneratingSuggestions = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(isGeneratingSuggestions = false)
            }
        }
    }

    fun useSuggestion(suggestion: String) {
        _uiState.value = _uiState.value.copy(
            currentMessage = suggestion,
            suggestedReplies = emptyList() // Clear suggestions after one is used
        )
    }
}