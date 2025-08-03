package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository

class SignUpUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, displayName: String) = repository.signUp(email, password, displayName)
}