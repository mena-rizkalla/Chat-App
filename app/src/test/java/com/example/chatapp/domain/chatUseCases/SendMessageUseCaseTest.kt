package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

class SendMessageUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var sendMessageUseCase: SendMessageUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        sendMessageUseCase = SendMessageUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success when sending a simple message`() = runBlocking {

        val receiverId = "user2"
        val messageText = "Hello, user2!"
        whenever(mockChatRepository.sendMessage(
            receiverId = receiverId,
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )).thenReturn(Result.success(Unit))

        val result = sendMessageUseCase(
            receiverId = receiverId,
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return success when sending a reply message`() = runBlocking {

        val receiverId = "user2"
        val messageText = "This is a reply."
        val repliedToId = "message123"
        val repliedToText = "Original message"
        val repliedToSender = "user1"
        whenever(mockChatRepository.sendMessage(
            receiverId = receiverId,
            text = messageText,
            repliedToMessageId = repliedToId,
            repliedToMessageText = repliedToText,
            repliedToSenderId = repliedToSender
        )).thenReturn(Result.success(Unit))

        val result = sendMessageUseCase(
            receiverId = receiverId,
            text = messageText,
            repliedToMessageId = repliedToId,
            repliedToMessageText = repliedToText,
            repliedToSenderId = repliedToSender
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runBlocking {

        val receiverId = "user2"
        val messageText = "This will fail."
        val expectedException = RuntimeException("Failed to send message")
        whenever(mockChatRepository.sendMessage(any(), any(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(expectedException))

        val result = sendMessageUseCase(
            receiverId = receiverId,
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}