package com.example.chatapp.presentation.signUpScreen

sealed interface SignUpAction {
    data class OnDisplayNameChange(val name: String) : SignUpAction
    data class OnEmailChange(val email: String) : SignUpAction
    data class OnPasswordChange(val password: String) : SignUpAction
    data object SignUp : SignUpAction
    data object NavigateBack : SignUpAction
    data object NavigateToLogin : SignUpAction
}