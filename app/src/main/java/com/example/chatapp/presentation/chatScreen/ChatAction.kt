package com.example.chatapp.presentation.chatScreen

import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.presentation.globalChatScreen.UiMessage

sealed interface ChatAction {
    data class OnMessageChange(val message: String) : ChatAction
    data object SendMessage : ChatAction
    data object GenerateReplySuggestions : ChatAction
    data class UseSuggestion(val suggestion: String) : ChatAction

    // Message Actions (from long-press or bottom sheet)
    data class OnMessageLongPress(val uiMessage: UiMessage) : ChatAction
    data object DismissMessageActions : ChatAction
    data class ToggleReaction(val messageId: String, val reaction: Reaction) : ChatAction

    // Reply Actions
    data class StartReply(val uiMessage: UiMessage) : ChatAction
    data object CancelReply : ChatAction

    // Edit Actions
    data object StartEdit : ChatAction
    data object CancelEdit : ChatAction
    data object ConfirmEdit : ChatAction

    // Delete Actions
    data object StartDelete : ChatAction
    data object CancelDelete : ChatAction
    data object ConfirmDelete : ChatAction

    // Navigation
    data object NavigateBack : ChatAction
}