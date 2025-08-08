package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Reaction

class ToggleGlobalMessageReactionUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(messageId: String, reaction: Reaction) =
        repository.toggleReactionOnGlobalMessage(messageId, reaction)
}