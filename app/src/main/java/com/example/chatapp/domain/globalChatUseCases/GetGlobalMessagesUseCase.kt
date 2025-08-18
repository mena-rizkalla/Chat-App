package com.example.chatapp.domain.globalChatUseCases

import com.example.chatapp.domain.ChatRepository

class GetGlobalMessagesUseCase(private val repository: ChatRepository) {
    operator fun invoke() = repository.getGlobalChatMessages()
}