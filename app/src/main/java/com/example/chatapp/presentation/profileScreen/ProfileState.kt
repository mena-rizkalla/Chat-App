package com.example.chatapp.presentation.profileScreen

import com.example.chatapp.domain.model.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false
)
