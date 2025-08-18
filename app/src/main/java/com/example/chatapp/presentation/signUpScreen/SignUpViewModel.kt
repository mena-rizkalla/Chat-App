package com.example.chatapp.presentation.signUpScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.SignUpUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(private val signUpUseCase: SignUpUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<SignUpEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun onAction(action: SignUpAction) {
        when(action) {
            is SignUpAction.OnDisplayNameChange -> onDisplayNameChange(action.name)
            is SignUpAction.OnEmailChange -> onEmailChange(action.email)
            is SignUpAction.OnPasswordChange -> onPasswordChange(action.password)
            is SignUpAction.SignUp -> signUp()
            is SignUpAction.NavigateBack -> navigateBack()
            is SignUpAction.NavigateToLogin -> navigateToLogin()
        }
    }
    private fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }
    private fun onDisplayNameChange(name: String) {
        _uiState.update { it.copy(displayName = name, error = null) }
    }
    private fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, error = null) }
    }

    private fun signUp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            signUpUseCase(
                email = uiState.value.email,
                password = uiState.value.password,
                displayName = uiState.value.displayName
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _eventFlow.emit(SignUpEvent.NavigateToMain)
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false, error = exception.message) }
            }
        }
    }

    private fun navigateBack() = viewModelScope.launch {
        _eventFlow.emit(SignUpEvent.NavigateBack)
    }

    private fun navigateToLogin() = viewModelScope.launch {
        _eventFlow.emit(SignUpEvent.NavigateToLogin)
    }
}