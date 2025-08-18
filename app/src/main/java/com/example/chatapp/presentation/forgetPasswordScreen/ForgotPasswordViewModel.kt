package com.example.chatapp.presentation.forgetPasswordScreen

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.SendPasswordResetUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val sendPasswordResetUseCase: SendPasswordResetUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordState())
    val uiState = _uiState.asStateFlow()
    private val _eventFlow = MutableSharedFlow<ForgotPasswordEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChange -> onEmailChange(action.email)
            is ForgotPasswordAction.SendResetLink -> sendPasswordResetEmail()
            is ForgotPasswordAction.NavigateBack -> navigateBack()
        }
    }
    private fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email) }

    private fun sendPasswordResetEmail() {
        val email = uiState.value.email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            sendPasswordResetUseCase(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventFlow.emit(ForgotPasswordEvent.ResetLinkSent)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }
    private fun navigateBack() {
        viewModelScope.launch {
            _eventFlow.emit(ForgotPasswordEvent.NavigateBack)
        }
    }
}