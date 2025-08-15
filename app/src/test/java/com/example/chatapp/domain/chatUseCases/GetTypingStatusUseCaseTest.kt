package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class GetTypingStatusUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var getTypingStatusUseCase: GetTypingStatusUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getTypingStatusUseCase = GetTypingStatusUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return true when user is typing`() = runBlocking {

        val dummyReceiverId = "user2"
        whenever(mockChatRepository.getTypingStatus(any())).thenReturn(flowOf(true))

        val typingStatusFlow = getTypingStatusUseCase(dummyReceiverId)
        val isTyping = typingStatusFlow.first()

        assertEquals(true, isTyping)
    }

    @Test
    fun `invoke should return false when user is not typing`() = runBlocking {

        val dummyReceiverId = "user3"
        whenever(mockChatRepository.getTypingStatus(any())).thenReturn(flowOf(false))

        val typingStatusFlow = getTypingStatusUseCase(dummyReceiverId)
        val isTyping = typingStatusFlow.first()

        assertEquals(false, isTyping)
    }

    @Test
    fun `invoke should propagate error when repository fails`() {

        val dummyReceiverId = "user4"
        val expectedException = RuntimeException("Connection error")
        val errorFlow = flow<Boolean> { throw expectedException }
        whenever(mockChatRepository.getTypingStatus(any())).thenReturn(errorFlow)

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking {
                getTypingStatusUseCase(dummyReceiverId).first()
            }
        }
        assertEquals("Connection error", exception.message)
    }
}