package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class DeleteMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String, messageId: String) =
        repository.deleteMessage(receiverId, messageId)
}