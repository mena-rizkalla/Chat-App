package com.example.chatapp.presentation.forgetPasswordScreen

sealed interface ForgotPasswordEvent {
    data object NavigateBack : ForgotPasswordEvent
    data object ResetLinkSent : ForgotPasswordEvent
}