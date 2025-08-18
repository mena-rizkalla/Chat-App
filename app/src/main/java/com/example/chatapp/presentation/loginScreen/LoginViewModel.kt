package com.example.chatapp.presentation.loginScreen

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.authUseCases.SignInUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<LoginEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChange -> onEmailChange(action.email)
            is LoginAction.OnPasswordChange -> onPasswordChange(action.password)
            is LoginAction.SignIn -> signIn()
            is LoginAction.NavigateToSignUp -> navigateToSignUp()
            is LoginAction.NavigateToForgotPassword -> navigateToForgotPassword()
        }
    }

    private fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    private fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    private fun signIn() {
        if (!Patterns.EMAIL_ADDRESS.matcher(uiState.value.email).matches()) {
            _uiState.update { it.copy(error = "Please enter a valid email address.") }
            return
        }
        if (uiState.value.password.isBlank()) {
            _uiState.update { it.copy(error = "Password cannot be empty.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signInUseCase(uiState.value.email, uiState.value.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isSignedIn = true) }
                    _eventFlow.emit(LoginEvent.NavigateToMain)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false, error = exception.message) }
                }
        }
    }

    private fun navigateToSignUp() = viewModelScope.launch {
        _eventFlow.emit(LoginEvent.NavigateToSignUp)
    }

    private fun navigateToForgotPassword() = viewModelScope.launch {
        _eventFlow.emit(LoginEvent.NavigateToForgotPassword)
    }
}
