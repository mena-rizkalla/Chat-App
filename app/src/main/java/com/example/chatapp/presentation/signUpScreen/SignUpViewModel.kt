package com.example.chatapp.presentation.signUpScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.SignUpUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(private val signUpUseCase: SignUpUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) { _uiState.value = _uiState.value.copy(email = email) }
    fun onDisplayNameChange(name: String) { _uiState.value = _uiState.value.copy(displayName = name) }
    fun onPasswordChange(password: String) { _uiState.value = _uiState.value.copy(password = password) }

    fun signUp(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val result = signUpUseCase(uiState.value.email, uiState.value.password, uiState.value.displayName)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(isLoading = false, isSignedUp = true)
                onSuccess()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
            }
        }
    }
}