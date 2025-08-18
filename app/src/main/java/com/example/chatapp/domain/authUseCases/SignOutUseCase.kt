package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository

class SignOutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() = repository.signOut()
}