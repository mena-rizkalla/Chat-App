package com.example.chatapp.presentation.usersScreen

import com.example.chatapp.domain.model.User

data class UsersState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)