package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke() = repository.currentUser
}