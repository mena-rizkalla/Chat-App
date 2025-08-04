package com.example.chatapp.presentation.signUpScreen

data class SignUpState(
    val email: String = "",
    val displayName: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSignedUp: Boolean = false,
    val error: String? = null
)