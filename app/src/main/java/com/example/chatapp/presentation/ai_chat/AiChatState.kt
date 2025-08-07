package com.example.chatapp.presentation.ai_chat

import com.example.chatapp.domain.model.Message

data class AiChatState(
    val messages: List<Message> = emptyList(),
    val currentMessage: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)