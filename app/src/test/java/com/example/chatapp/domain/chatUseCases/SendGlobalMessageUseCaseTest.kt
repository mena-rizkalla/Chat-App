package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import kotlin.test.Test

class SendGlobalMessageUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var sendGlobalMessageUseCase: SendGlobalMessageUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        sendGlobalMessageUseCase = SendGlobalMessageUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success when sending a simple message`() = runBlocking {

        val messageText = "Hello, world!"
        whenever(mockChatRepository.sendGlobalMessage(
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )).thenReturn(Result.success(Unit))

        val result = sendGlobalMessageUseCase(
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return success when sending a reply message`() = runBlocking {

        val messageText = "This is a reply."
        val repliedToId = "message123"
        val repliedToText = "Original message"
        val repliedToSender = "user1"
        whenever(mockChatRepository.sendGlobalMessage(
            text = messageText,
            repliedToMessageId = repliedToId,
            repliedToMessageText = repliedToText,
            repliedToSenderId = repliedToSender
        )).thenReturn(Result.success(Unit))

        val result = sendGlobalMessageUseCase(
            text = messageText,
            repliedToMessageId = repliedToId,
            repliedToMessageText = repliedToText,
            repliedToSenderId = repliedToSender
        )

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails`() = runBlocking {

        val messageText = "This will fail."
        val expectedException = RuntimeException("Failed to send message")
        whenever(mockChatRepository.sendGlobalMessage(anyOrNull(), anyOrNull(), anyOrNull(), anyOrNull()))
            .thenReturn(Result.failure(expectedException))

        val result = sendGlobalMessageUseCase(
            text = messageText,
            repliedToMessageId = null,
            repliedToMessageText = null,
            repliedToSenderId = null
        )

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}