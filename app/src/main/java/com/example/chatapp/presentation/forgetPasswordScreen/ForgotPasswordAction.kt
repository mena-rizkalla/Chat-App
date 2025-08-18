package com.example.chatapp.presentation.forgetPasswordScreen

sealed interface ForgotPasswordAction {
    data class OnEmailChange(val email: String) : ForgotPasswordAction
    data object SendResetLink : ForgotPasswordAction
    data object NavigateBack : ForgotPasswordAction
}