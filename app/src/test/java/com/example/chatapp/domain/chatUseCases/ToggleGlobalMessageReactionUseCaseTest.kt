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

class ToggleGlobalMessageReactionUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var toggleGlobalMessageReactionUseCase: ToggleGlobalMessageReactionUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        toggleGlobalMessageReactionUseCase = ToggleGlobalMessageReactionUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success when repository successfully toggles reaction`() = runBlocking {

        val messageId = "globalMessage1"
        val reaction = Reaction.LIKE
        whenever(mockChatRepository.toggleReactionOnGlobalMessage(any(), any()))
            .thenReturn(Result.success(Unit))

        val result = toggleGlobalMessageReactionUseCase(messageId, reaction)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails to toggle reaction`() = runBlocking {

        val messageId = "globalMessage2"
        val reaction = Reaction.LOVE
        val expectedException = RuntimeException("Database error")
        whenever(mockChatRepository.toggleReactionOnGlobalMessage(any(), any()))
            .thenReturn(Result.failure(expectedException))

        val result = toggleGlobalMessageReactionUseCase(messageId, reaction)

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}