package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository

class SendPasswordResetUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String) = repository.sendPasswordResetEmail(email)
}