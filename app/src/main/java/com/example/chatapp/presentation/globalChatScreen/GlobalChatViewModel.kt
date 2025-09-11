package com.example.chatapp.presentation.globalChatScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.globalChatUseCases.GetGlobalMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetUsersUseCase
import com.example.chatapp.domain.globalChatUseCases.SendGlobalMessageUseCase
import com.example.chatapp.domain.chatUseCases.ToggleGlobalMessageReactionUseCase
import com.example.chatapp.domain.geminiUseCase.GetGeminiResponseUseCase
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Reaction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GlobalChatViewModel(
    private val getGlobalMessagesUseCase: GetGlobalMessagesUseCase,
    private val sendGlobalMessageUseCase: SendGlobalMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUsersUseCase: GetUsersUseCase,
    private val toggleReactionUseCase: ToggleGlobalMessageReactionUseCase,
    private val getGeminiResponseUseCase: GetGeminiResponseUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(GlobalChatState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<GlobalChatEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadData()
    }

    fun onAction(action: GlobalChatAction) {
        when (action) {
            is GlobalChatAction.OnMessageChange -> onMessageChange(action.message)
            is GlobalChatAction.SendMessage -> sendMessage()
            is GlobalChatAction.StartReply -> onStartReply(action.uiMessage)
            is GlobalChatAction.CancelReply -> onCancelReply()
            is GlobalChatAction.OnMessageLongPress -> onMessageLongPress(action.messageId)
            is GlobalChatAction.ToggleReaction -> toggleReaction(action.reaction)
            is GlobalChatAction.DismissReactionPalette -> dismissReactionPalette()
            is GlobalChatAction.GenerateReplySuggestions -> generateReplySuggestions()
            is GlobalChatAction.UseSuggestion -> useSuggestion(action.suggestion)
            is GlobalChatAction.NavigateBack -> navigateBack()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            // 1. Set loading state and get current user ID
            val currentUser = getCurrentUserUseCase()
            val currentUserId = currentUser?.uid ?: ""
            _uiState.value = _uiState.value.copy(isLoading = true, currentUserId = currentUserId)


            Log.d("GlobalChatViewModel", "Current User ID set to: $currentUserId")


            // 2. Fetch the list of users first.
            getUsersUseCase().onSuccess { users ->
                val userMap = users.associateBy({ it.uid }, { it.displayName }).toMutableMap()
                Log.d("GlobalChatViewModel", "User Map Loaded: $userMap")
                if (currentUser != null) {
                    userMap[currentUser.uid] = currentUser.displayName
                }
                // 3. If users are fetched successfully, start listening for messages.
                getGlobalMessagesUseCase().onEach { messages ->
                    val uiMessages = messages.map { message ->

                        val senderName = if (message.senderId == currentUserId) {
                            "You"
                        } else {
                            userMap[message.senderId] ?: "Unknown User"
                        }

                        val repliedToName = if (message.repliedToSenderId == currentUserId) {
                            "You"
                        } else {
                            userMap[message.repliedToSenderId]
                        }

                        UiMessage(
                            message = message,
                            senderDisplayName = senderName,
                            repliedToSenderName = repliedToName
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        messages = uiMessages,
                        isLoading = false // Turn off loading once we have messages
                    )
                }.catch { e ->
                    Log.e("GlobalChatViewModel", "Error listening for messages", e)
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }.launchIn(viewModelScope)

            }.onFailure { e ->
                Log.e("GlobalChatViewModel", "Error fetching users", e)
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    private fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
    }

    private fun sendMessage() {
        viewModelScope.launch {
            val text = uiState.value.currentMessage
            val replyingTo = uiState.value.replyingToMessage
            if (text.isNotBlank()) {
                _uiState.value = _uiState.value.copy(currentMessage = "",replyingToMessage = null) // Clear input
                sendGlobalMessageUseCase(
                    text,
                    repliedToMessageId = replyingTo?.message?.messageId,
                    repliedToMessageText = replyingTo?.message?.text,
                    repliedToSenderId = replyingTo?.message?.senderId
                )
            }
        }
    }

    private fun toggleReaction(reaction: Reaction)  {
        val messageId = uiState.value.selectedMessageIdForReaction

        if (messageId != null) {
            // 2. Dismiss the palette right away for an instant UI update
            dismissReactionPalette()

            // 3. Launch the network call in the background
            viewModelScope.launch {
                toggleReactionUseCase(messageId = messageId, reaction = reaction)
            }
        }
    }
    private fun dismissReactionPalette() {
        _uiState.update { it.copy(selectedMessageIdForReaction = null) }
    }

    private fun generateReplySuggestions() {
        val lastMessage = uiState.value.messages.lastOrNull { it.message.senderId != uiState.value.currentUserId }
        if (lastMessage == null || uiState.value.isGeneratingSuggestions) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingSuggestions = true, suggestedReplies = emptyList())
            val prompt = "Based on the message from ${lastMessage.senderDisplayName}: \"${lastMessage.message.text}\", suggest three short, distinct, and natural-sounding replies. Format them as a simple numbered list without any extra text."
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

    private fun useSuggestion(suggestion: String) {
        _uiState.value = _uiState.value.copy(
            currentMessage = suggestion,
            suggestedReplies = emptyList()
        )
    }

    private fun onStartReply(uiMessage: UiMessage) {
         _uiState.value = _uiState.value.copy(replyingToMessage = uiMessage)
    }

    private fun onCancelReply() {
         _uiState.value = _uiState.value.copy(replyingToMessage = null)
    }

    private fun onMessageLongPress(messageId: String) {
        _uiState.update { it.copy(selectedMessageIdForReaction = messageId) }
    }

    private fun navigateBack() = viewModelScope.launch {
        _eventFlow.emit(GlobalChatEvent.NavigateBack)
    }
}