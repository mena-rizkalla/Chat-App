package com.example.chatapp.presentation.usersScreen

sealed interface UsersEvent {
    data class NavigateToChat(val receiverId: String, val receiverName: String) : UsersEvent
}