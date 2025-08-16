package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class EditMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String, messageId: String, newText: String) =
        repository.editMessage(receiverId, messageId, newText)
}