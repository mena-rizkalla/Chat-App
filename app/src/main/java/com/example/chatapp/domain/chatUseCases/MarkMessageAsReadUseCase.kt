package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class MarkMessageAsReadUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String, messageId: String) = repository.markMessagesAsRead(receiverId, messageId)
}