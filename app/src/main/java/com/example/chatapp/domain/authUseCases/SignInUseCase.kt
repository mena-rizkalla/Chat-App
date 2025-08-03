package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository

class SignInUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String) = repository.signIn(email, password)
}