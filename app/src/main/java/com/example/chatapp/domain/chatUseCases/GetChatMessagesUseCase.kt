package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class GetChatMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke(receiverId: String) = repository.getChatMessages(receiverId)
}