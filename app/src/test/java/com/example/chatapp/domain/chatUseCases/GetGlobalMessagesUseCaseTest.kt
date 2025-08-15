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
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetGlobalMessagesUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var getGlobalMessagesUseCase: GetGlobalMessagesUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getGlobalMessagesUseCase = GetGlobalMessagesUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return flow of global messages when repository succeeds`() = runBlocking {

        val expectedMessages = listOf(
            Message(messageId = "g1", text = "Welcome everyone!", senderId = "user1"),
            Message(messageId = "g2", text = "Hello from the global chat!", senderId = "user2")
        )
        whenever(mockChatRepository.getGlobalChatMessages()).thenReturn(flowOf(expectedMessages))

        val messagesFlow = getGlobalMessagesUseCase()
        val actualMessages = messagesFlow.first()

        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `invoke should return an empty flow when there are no global messages`() = runBlocking {

        val expectedMessages = emptyList<Message>()
        whenever(mockChatRepository.getGlobalChatMessages()).thenReturn(flowOf(expectedMessages))

        val messagesFlow = getGlobalMessagesUseCase()
        val actualMessages = messagesFlow.first()

        assertEquals(expectedMessages, actualMessages)
    }

    @Test
    fun `invoke should propagate error when repository fails`() {

        val expectedException = RuntimeException("Network error")
        val errorFlow = flow<List<Message>> { throw expectedException }
        whenever(mockChatRepository.getGlobalChatMessages()).thenReturn(errorFlow)

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking {
                getGlobalMessagesUseCase().first()
            }
        }
        assertEquals("Network error", exception.message)
    }
}