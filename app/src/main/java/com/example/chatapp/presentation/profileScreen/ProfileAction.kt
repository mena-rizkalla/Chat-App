package com.example.chatapp.presentation.profileScreen

sealed interface ProfileAction {
    data object SignOut : ProfileAction
}