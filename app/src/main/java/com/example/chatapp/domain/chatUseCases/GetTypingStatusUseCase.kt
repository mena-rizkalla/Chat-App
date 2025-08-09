package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class GetTypingStatusUseCase(private val repository: ChatRepository) {
    operator fun invoke(receiverId: String) = repository.getTypingStatus(receiverId)
}