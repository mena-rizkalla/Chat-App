package com.example.chatapp.domain.globalChatUseCases

import com.example.chatapp.domain.ChatRepository

class SendGlobalMessageUseCase(private val repository: ChatRepository) {
    suspend operator fun invoke(
        text: String,
        repliedToMessageId: String?,
        repliedToMessageText: String?,
        repliedToSenderId: String?) = repository.sendGlobalMessage(text, repliedToMessageId, repliedToMessageText, repliedToSenderId)
}