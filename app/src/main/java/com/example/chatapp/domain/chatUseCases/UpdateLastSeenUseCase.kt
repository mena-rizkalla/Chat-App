package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class UpdateLastSeenUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String) = repository.updateLastSeenTimestamp(receiverId)
}