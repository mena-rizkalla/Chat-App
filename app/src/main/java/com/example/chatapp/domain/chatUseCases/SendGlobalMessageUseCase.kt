package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class SendGlobalMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(text: String) = repository.sendGlobalMessage(text)
}