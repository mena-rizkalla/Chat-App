package com.example.chatapp.presentation.profileScreen

sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}