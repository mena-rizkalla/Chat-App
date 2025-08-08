package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Reaction

class TogglePrivateMessageReactionUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(receiverId: String, messageId: String, reaction: Reaction) =
        repository.toggleReactionOnPrivateMessage(receiverId, messageId, reaction)
}