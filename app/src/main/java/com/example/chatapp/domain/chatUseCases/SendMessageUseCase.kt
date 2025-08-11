package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository

class SendMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(
        receiverId: String,
        text: String,
        repliedToMessageId: String?,
        repliedToMessageText: String?,
        repliedToSenderId: String?
        ) = repository.sendMessage(receiverId, text, repliedToMessageId, repliedToMessageText, repliedToSenderId)
}