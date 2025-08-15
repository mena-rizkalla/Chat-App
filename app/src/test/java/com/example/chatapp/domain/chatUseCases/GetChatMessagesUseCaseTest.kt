package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Message
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class GetChatMessagesUseCaseTest {
    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var getChatMessagesUseCase: GetChatMessagesUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        getChatMessagesUseCase = GetChatMessagesUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return flow of messages from repository`() = runBlocking {

        val dummyReceiverId = "user2"
        val expectedMessages = listOf(
            Message(messageId = "1", text = "Hello", senderId = "user1"),
            Message(messageId = "2", text = "Hi there", senderId = "user2")
        )

        whenever(mockChatRepository.getChatMessages(any())).thenReturn(flowOf(expectedMessages))


        val messagesFlow = getChatMessagesUseCase(dummyReceiverId)
        val actualMessages = messagesFlow.first()


        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `invoke should return an empty flow when repository returns no messages`() = runBlocking {

        val dummyReceiverId = "user3"
        val expectedMessages = emptyList<Message>()
        whenever(mockChatRepository.getChatMessages(any())).thenReturn(flowOf(expectedMessages))


        val messagesFlow = getChatMessagesUseCase(dummyReceiverId)
        val actualMessages = messagesFlow.first()


        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `invoke should propagate error when repository returns an error flow`() {

        val dummyReceiverId = "user4"
        val expectedException = RuntimeException("Database connection failed")

        val errorFlow = flow<List<Message>> { throw expectedException }
        whenever(mockChatRepository.getChatMessages(any())).thenReturn(errorFlow)

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking {
                getChatMessagesUseCase(dummyReceiverId).first()
            }
        }
        assertEquals("Database connection failed", exception.message)
    }

}