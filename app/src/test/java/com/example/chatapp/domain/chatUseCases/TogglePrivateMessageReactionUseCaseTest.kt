package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Reaction
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.Test

class TogglePrivateMessageReactionUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var togglePrivateMessageReactionUseCase: TogglePrivateMessageReactionUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        togglePrivateMessageReactionUseCase = TogglePrivateMessageReactionUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success when repository successfully toggles reaction`() = runBlocking {
        // Arrange
        val receiverId = "user2"
        val messageId = "privateMessage1"
        val reaction = Reaction.WOW
        whenever(mockChatRepository.toggleReactionOnPrivateMessage(any(), any(), any()))
            .thenReturn(Result.success(Unit))

        // Act
        val result = togglePrivateMessageReactionUseCase(receiverId, messageId, reaction)

        // Assert
        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails to toggle reaction`() = runBlocking {
        // Arrange
        val receiverId = "user2"
        val messageId = "privateMessage2"
        val reaction = Reaction.SAD
        val expectedException = RuntimeException("User not found")
        whenever(mockChatRepository.toggleReactionOnPrivateMessage(any(), any(), any()))
            .thenReturn(Result.failure(expectedException))

        // Act
        val result = togglePrivateMessageReactionUseCase(receiverId, messageId, reaction)

        // Assert
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}
