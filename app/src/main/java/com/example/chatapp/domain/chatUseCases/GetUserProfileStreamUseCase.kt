package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository


class GetUserProfileStreamUseCase(private val repository: ChatRepository) {
    operator fun invoke(uid: String) = repository.getUserProfileStream(uid)
}
