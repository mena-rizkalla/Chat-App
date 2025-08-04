package com.example.chatapp.presentation.globalChatScreen

import com.example.chatapp.domain.model.Message

data class GlobalChatState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val currentUserId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)