package com.example.chatapp.presentation.loginScreen

sealed interface LoginAction {
    data class OnEmailChange(val email: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
    data object SignIn : LoginAction
    data object NavigateToSignUp : LoginAction
    data object NavigateToForgotPassword : LoginAction
}