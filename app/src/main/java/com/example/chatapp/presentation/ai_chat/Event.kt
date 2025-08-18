package com.example.chatapp.presentation.ai_chat

sealed interface Event {
    data object NavigateBack : Event
}