package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class GetOnlineUsersUseCase(private val repository: ChatRepository) {
    operator fun invoke() = repository.getOnlineUsers()
}