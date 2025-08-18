package com.example.chatapp.presentation.chatScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.DeleteMessageUseCase
import com.example.chatapp.domain.chatUseCases.EditMessageUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetLastSeenUseCase
import com.example.chatapp.domain.chatUseCases.GetTypingStatusUseCase
import com.example.chatapp.domain.chatUseCases.GetUserProfileStreamUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import com.example.chatapp.domain.chatUseCases.TogglePrivateMessageReactionUseCase
import com.example.chatapp.domain.chatUseCases.UpdateLastSeenUseCase
import com.example.chatapp.domain.chatUseCases.UpdateTypingStatusUseCase
import com.example.chatapp.domain.geminiUseCase.GetGeminiResponseUseCase
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.presentation.globalChatScreen.UiMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val receiverName: String,
    private val getChatMessagesUseCase: GetChatMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val toggleReactionUseCase: TogglePrivateMessageReactionUseCase,
    private val getTypingStatusUseCase: GetTypingStatusUseCase,
    private val updateTypingStatusUseCase: UpdateTypingStatusUseCase,
    private val getGeminiResponseUseCase: GetGeminiResponseUseCase,
    private val updateLastSeenUseCase: UpdateLastSeenUseCase,
    private val getLastSeenUseCase: GetLastSeenUseCase,
    private val editMessageUseCase: EditMessageUseCase,
    private val deleteMessageUseCase: DeleteMessageUseCase,
    private val getUserProfileStreamUseCase: GetUserProfileStreamUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<ChatEvent>()
    val eventFlow = _eventFlow.asSharedFlow()
    private val textInputFlow = MutableStateFlow("")
    private val userNames = mutableMapOf<String, String>()

    init {
        val currentUser = getCurrentUserUseCase()
        if (currentUser != null) {
            _uiState.value = _uiState.value.copy(currentUserId = currentUser.uid)
            // Create the map to look up names from IDs
            userNames[currentUser.uid] = "You"
            userNames[receiverId] = receiverName
        }

        getUserProfileStreamUseCase(receiverId)
            .onEach { user ->
                if (user != null) {
                    _uiState.update { it.copy(receiverLastSeenTimestamp = user.lastSeenTimestamp) }
                }
            }.launchIn(viewModelScope)

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
            .onEach { messages -> // This is a List<Message> from the database
                // Map to UiMessage for the UI
                val uiMessages = messages.map { msg ->
                    UiMessage(
                        message = msg,
                        senderDisplayName = userNames[msg.senderId] ?: "Unknown",
                        repliedToSenderName = userNames[msg.repliedToSenderId]
                    )
                }
                // Update the UI state with the mapped messages
                _uiState.update { it.copy(messages = uiMessages) }

                // After updating the UI, check if the last message
                // is from the other user.
                if (messages.lastOrNull()?.senderId == receiverId) {
                    // If it is, it means we just "saw" a new message.
                    // Immediately update our own "last seen" timestamp to signal this.
                    updateLastSeenUseCase(receiverId)
                }
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

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.OnMessageChange -> onMessageChange(action.message)
            is ChatAction.SendMessage -> sendMessage()
            is ChatAction.ToggleReaction -> toggleReaction(action.messageId, action.reaction)
            is ChatAction.GenerateReplySuggestions -> generateReplySuggestions()
            is ChatAction.UseSuggestion -> useSuggestion(action.suggestion)
            is ChatAction.OnMessageLongPress -> onMessageActionSelected(action.uiMessage)
            is ChatAction.DismissMessageActions -> onDismissMessageActions()
            is ChatAction.StartReply -> onStartReply(action.uiMessage)
            is ChatAction.CancelReply -> onCancelReply()
            is ChatAction.StartEdit -> onStartEdit()
            is ChatAction.CancelEdit -> onCancelEdit()
            is ChatAction.ConfirmEdit -> onConfirmEdit()
            is ChatAction.StartDelete -> onStartDelete()
            is ChatAction.CancelDelete -> onCancelDelete()
            is ChatAction.ConfirmDelete -> onConfirmDelete()
            is ChatAction.NavigateBack -> navigateBack()
        }
    }

    private fun navigateBack() = viewModelScope.launch { _eventFlow.emit(ChatEvent.NavigateBack) }

    private fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
        textInputFlow.value = message
    }

    private fun sendMessage() {
        viewModelScope.launch {
            val text = uiState.value.currentMessage
            val replyingTo = uiState.value.replyingToMessage
            if (text.isNotBlank()) {
                sendMessageUseCase(
                    receiverId,
                    text,
                    repliedToMessageId = replyingTo?.message?.messageId,
                    repliedToMessageText = replyingTo?.message?.text,
                    repliedToSenderId = replyingTo?.message?.senderId
                )
                _uiState.value = _uiState.value.copy(
                    currentMessage = "", // Clear input
                    replyingToMessage = null
                )
                textInputFlow.value = ""
                updateTypingStatusUseCase(receiverId, false)
            }
        }
    }

    private fun toggleReaction(messageId: String, reaction: Reaction) {
        viewModelScope.launch {
            toggleReactionUseCase(
                receiverId = receiverId,
                messageId = messageId,
                reaction = reaction
            )
            // No need to update state here, Firestore's snapshot listener will do it for us.
        }
    }

    private fun generateReplySuggestions() {
        val lastMessage = uiState.value.messages.lastOrNull { it.message.senderId != uiState.value.currentUserId }
        if (lastMessage == null || uiState.value.isGeneratingSuggestions) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingSuggestions = true, suggestedReplies = emptyList())

            val prompt = "Based on the message: \"${lastMessage.message.text}\", suggest three short, distinct, and natural-sounding replies. Format them as a simple numbered list without any extra text."
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
            suggestedReplies = emptyList() // Clear suggestions after one is used
        )
    }

    private fun onStartReply(uiMessage: UiMessage) {
        _uiState.value = _uiState.value.copy(replyingToMessage = uiMessage , messageForAction = null)
    }

    private fun onCancelReply() {
        _uiState.value = _uiState.value.copy(replyingToMessage = null)
    }

    private fun onMessageActionSelected(uiMessage: UiMessage) {
        _uiState.value = _uiState.value.copy(messageForAction = uiMessage)
    }

    private fun onDismissMessageActions() {
        _uiState.value = _uiState.value.copy(messageForAction = null)
    }

    private fun onStartEdit() {
        val messageToEdit = uiState.value.messageForAction
        _uiState.value = _uiState.value.copy(
            editingMessage = messageToEdit,
            currentMessage = messageToEdit?.message?.text ?: "", // Pre-fill the input field
            messageForAction = null
        )
    }

    private fun onCancelEdit() {
        _uiState.value = _uiState.value.copy(editingMessage = null, currentMessage = "")
    }

    private fun onConfirmEdit() {
        viewModelScope.launch {
            val messageToEdit = uiState.value.editingMessage
            val newText = uiState.value.currentMessage
            if (messageToEdit != null && newText.isNotBlank()) {
                editMessageUseCase(receiverId, messageToEdit.message.messageId, newText)
                onCancelEdit() // Clear the editing state
            }
        }
    }

    private fun onStartDelete() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = true)
    }

    private fun onCancelDelete() {
        _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = false, messageForAction = null)
    }

    private fun onConfirmDelete() {
        viewModelScope.launch {
            val messageToDelete = uiState.value.messageForAction
            if (messageToDelete != null) {
                deleteMessageUseCase(receiverId, messageToDelete.message.messageId)
                _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = false, messageForAction = null)
            }
            _uiState.value = _uiState.value.copy(showDeleteConfirmDialog = false,messageForAction = null)

        }
    }
}