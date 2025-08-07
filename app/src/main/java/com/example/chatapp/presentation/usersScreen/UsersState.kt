package com.example.chatapp.presentation.usersScreen

import com.example.chatapp.domain.model.User

data class UsersState(
    val currentUser: User = User(),
    val users: List<User> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)