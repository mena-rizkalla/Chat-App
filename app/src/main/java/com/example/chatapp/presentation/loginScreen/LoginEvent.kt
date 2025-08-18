package com.example.chatapp.presentation.loginScreen

sealed interface LoginEvent {
    data object NavigateToMain : LoginEvent
    data object NavigateToSignUp : LoginEvent
    data object NavigateToForgotPassword : LoginEvent
}