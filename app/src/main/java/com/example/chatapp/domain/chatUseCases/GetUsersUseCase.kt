package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class GetUsersUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke() = repository.getUsers()
}