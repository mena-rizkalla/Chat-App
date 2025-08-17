package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test

class DeleteMessageUseCaseTest {

    private lateinit var mockChatRepository: ChatRepository
    private lateinit var deleteMessageUseCase: DeleteMessageUseCase

    @BeforeEach
    fun setUp() {
        mockChatRepository = mock()
        deleteMessageUseCase = DeleteMessageUseCase(mockChatRepository)
    }


    @Test
    fun `invoke should call repository deleteMessage and return success`() = runBlocking {
        // ARRANGE: Define test data and mock behavior.
        val testReceiverId = "receiver_123"
        val testMessageId = "message_456"
        // Configure the mock to return a success result.
        whenever(mockChatRepository.deleteMessage(testReceiverId, testMessageId))
            .thenReturn(Result.success(Unit))

        // ACT: Execute the use case.
        val result = deleteMessageUseCase(testReceiverId, testMessageId)

        // ASSERT: Verify the interactions and the outcome.
        // Ensure the repository's deleteMessage method was called with the correct IDs.
        verify(mockChatRepository).deleteMessage(testReceiverId, testMessageId)
        // Ensure the result of the use case is a success.
        assert(result.isSuccess)
    }

    // 4. Test the failed message deletion scenario.
    @Test
    fun `invoke should call repository deleteMessage and return failure on error`() = runBlocking {
        // ARRANGE: Define test data and mock behavior for a failure case.
        val testReceiverId = "receiver_123"
        val testMessageId = "message_456"
        val testException = Exception("Permission denied")
        // Configure the mock to return a failure result.
        whenever(mockChatRepository.deleteMessage(testReceiverId, testMessageId))
            .thenReturn(Result.failure(testException))

        // ACT: Execute the use case.
        val result = deleteMessageUseCase(testReceiverId, testMessageId)

        // ASSERT: Verify the interactions and the failure outcome.
        // Ensure the repository's deleteMessage method was still called.
        verify(mockChatRepository).deleteMessage(testReceiverId, testMessageId)
        // Ensure the result is a failure.
        assert(result.isFailure)
        // Ensure the correct exception was propagated.
        assertEquals(testException, result.exceptionOrNull())
    }
}
