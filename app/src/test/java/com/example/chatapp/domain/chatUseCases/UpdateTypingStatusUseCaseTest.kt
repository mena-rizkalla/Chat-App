package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import kotlin.test.Test

class UpdateTypingStatusUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var updateTypingStatusUseCase: UpdateTypingStatusUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        updateTypingStatusUseCase = UpdateTypingStatusUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should call repository with isTyping true`() = runBlocking {

        val receiverId = "user2"
        val isTyping = true

        updateTypingStatusUseCase(receiverId, isTyping)

        verify(mockChatRepository).updateTypingStatus(receiverId, isTyping)
    }

    @Test
    fun `invoke should call repository with isTyping false`() = runBlocking {

        val receiverId = "user3"
        val isTyping = false

        updateTypingStatusUseCase(receiverId, isTyping)

        verify(mockChatRepository).updateTypingStatus(receiverId, isTyping)
    }
}
