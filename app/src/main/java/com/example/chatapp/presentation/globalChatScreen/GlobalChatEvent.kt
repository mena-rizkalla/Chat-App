package com.example.chatapp.presentation.globalChatScreen

sealed interface GlobalChatEvent {
    data object NavigateBack : GlobalChatEvent
}