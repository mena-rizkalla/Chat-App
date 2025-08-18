package com.example.chatapp.presentation.signUpScreen

sealed interface SignUpEvent {
    data object NavigateToMain : SignUpEvent
    data object NavigateBack : SignUpEvent
    data object NavigateToLogin : SignUpEvent
}