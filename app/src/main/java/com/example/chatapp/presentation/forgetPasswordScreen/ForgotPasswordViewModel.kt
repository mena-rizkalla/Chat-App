package com.example.chatapp.presentation.forgetPasswordScreen

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.SendPasswordResetUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val sendPasswordResetUseCase: SendPasswordResetUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email) }

    fun sendPasswordResetEmail(onSuccess: () -> Unit) {

        if (!Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches()) {
            _uiState.value = _uiState.value.copy(error = "Please enter a valid email address.")
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
            sendPasswordResetUseCase(uiState.value.email).onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Password reset email sent!")
                onSuccess()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }
}