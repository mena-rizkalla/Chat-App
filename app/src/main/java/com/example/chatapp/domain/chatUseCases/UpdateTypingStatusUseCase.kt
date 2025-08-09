package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class UpdateTypingStatusUseCase(private val repository: ChatRepository) {
    operator fun invoke(receiverId: String, isTyping: Boolean) = repository.updateTypingStatus(receiverId, isTyping)
}