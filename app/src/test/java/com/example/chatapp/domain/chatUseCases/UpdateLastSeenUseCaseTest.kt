package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.Test

class UpdateLastSeenUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var updateLastSeenUseCase: UpdateLastSeenUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        updateLastSeenUseCase = UpdateLastSeenUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success when repository successfully updates timestamp`() = runBlocking {

        val receiverId = "user2"
        whenever(mockChatRepository.updateLastSeenTimestamp(any()))
            .thenReturn(Result.success(Unit))

        val result = updateLastSeenUseCase(receiverId)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `invoke should return failure when repository fails to update timestamp`() = runBlocking {

        val receiverId = "user3"
        val expectedException = RuntimeException("Failed to update timestamp")
        whenever(mockChatRepository.updateLastSeenTimestamp(any()))
            .thenReturn(Result.failure(expectedException))

        val result = updateLastSeenUseCase(receiverId)

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}