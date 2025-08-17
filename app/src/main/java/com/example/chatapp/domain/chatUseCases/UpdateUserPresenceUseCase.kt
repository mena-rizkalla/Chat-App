package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class UpdateUserPresenceUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke() = repository.updateUserPresence()
}