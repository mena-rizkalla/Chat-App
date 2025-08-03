package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String, text: String) = repository.sendMessage(receiverId, text)
}