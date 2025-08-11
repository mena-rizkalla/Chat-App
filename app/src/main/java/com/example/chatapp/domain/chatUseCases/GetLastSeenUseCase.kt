package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class GetLastSeenUseCase(private val repository: ChatRepository) {
    operator fun invoke(receiverId: String) = repository.getLastSeenTimestamp(receiverId)
}