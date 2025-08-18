package com.example.chatapp.presentation.ai_chat

sealed interface Action {
    data class OnMessageChange(val message: String) : Action
    data object SendMessage : Action
    data object NavigateBack : Action
}