package com.example.chatapp.presentation.usersScreen

import com.example.chatapp.domain.model.User

sealed interface UsersAction {
    data class OnUserClick(val user: User) : UsersAction
}