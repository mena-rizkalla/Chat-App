package com.example.chatapp.presentation.chatScreen

sealed interface ChatEvent {
    data object NavigateBack : ChatEvent
}