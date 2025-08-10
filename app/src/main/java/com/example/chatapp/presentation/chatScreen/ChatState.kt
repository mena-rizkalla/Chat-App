package com.example.chatapp.presentation.chatScreen

import com.example.chatapp.domain.model.Message

data class ChatState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val currentUserId: String = "",
    val isOtherUserTyping: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val suggestedReplies: List<String> = emptyList(),
    val isGeneratingSuggestions: Boolean = false,
)
