package com.example.chatapp.presentation.forgetPasswordScreen

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val error: String? = null
)