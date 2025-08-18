package com.example.chatapp.presentation.globalChatScreen

import com.example.chatapp.domain.model.Reaction

sealed interface GlobalChatAction {
    // Message Input & Sending
    data class OnMessageChange(val message: String) : GlobalChatAction
    data object SendMessage : GlobalChatAction

    // Replying
    data class StartReply(val uiMessage: UiMessage) : GlobalChatAction
    data object CancelReply : GlobalChatAction

    // Reactions
    data class OnMessageLongPress(val messageId: String) : GlobalChatAction
    data class ToggleReaction(val reaction: Reaction) : GlobalChatAction
    data object DismissReactionPalette : GlobalChatAction

    // AI Suggestions
    data object GenerateReplySuggestions : GlobalChatAction
    data class UseSuggestion(val suggestion: String) : GlobalChatAction

    // Navigation
    data object NavigateBack : GlobalChatAction
}