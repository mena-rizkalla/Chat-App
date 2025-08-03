package com.example.chatapp.domain.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String? = null, // Null for global chat messages
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
