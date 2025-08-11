package com.example.chatapp.presentation.globalChatScreen

import com.example.chatapp.domain.model.Message

data class GlobalChatState(
    val messages: List<UiMessage> = emptyList(),
    val currentMessage: String = "",
    val currentUserId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val suggestedReplies: List<String> = emptyList(),
    val isGeneratingSuggestions: Boolean = false,
    val replyingToMessage: UiMessage? = null
)

data class UiMessage(
    val message: Message,
    val senderDisplayName: String,
    val repliedToSenderName: String? = null,
    val shouldShowSenderName: Boolean = false
)