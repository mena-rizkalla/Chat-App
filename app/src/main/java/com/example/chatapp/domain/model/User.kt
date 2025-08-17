package com.example.chatapp.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val lastSeenTimestamp: Long = 0L,
)