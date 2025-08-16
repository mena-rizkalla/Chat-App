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

class EditMessageUseCaseTest {

    private lateinit var mockChatRepository: ChatRepository
    private lateinit var editMessageUseCase: EditMessageUseCase


    @BeforeEach
    fun setUp() {
        mockChatRepository = mock()
        editMessageUseCase = EditMessageUseCase(mockChatRepository)
    }


    @Test
    fun `invoke should call repository editMessage and return success`() = runBlocking {
        // ARRANGE: Define test data and mock behavior.
        val testReceiverId = "receiver_123"
        val testMessageId = "message_456"
        val newText = "This is the updated message text."
        // Configure the mock to return a success result.
        whenever(mockChatRepository.editMessage(testReceiverId, testMessageId, newText))
            .thenReturn(Result.success(Unit))

        // ACT: Execute the use case.
        val result = editMessageUseCase(testReceiverId, testMessageId, newText)

        // ASSERT: Verify the interactions and the outcome.
        // Ensure the repository's editMessage method was called with the correct arguments.
        verify(mockChatRepository).editMessage(testReceiverId, testMessageId, newText)
        // Ensure the result of the use case is a success.
        assert(result.isSuccess)
    }

    // 4. Test the failed message edit scenario.
    @Test
    fun `invoke should call repository editMessage and return failure on error`() = runBlocking {
        // ARRANGE: Define test data and mock behavior for a failure case.
        val testReceiverId = "receiver_123"
        val testMessageId = "message_456"
        val newText = "This will fail to update."
        val testException = Exception("You do not have permission to edit this message.")
        // Configure the mock to return a failure result.
        whenever(mockChatRepository.editMessage(testReceiverId, testMessageId, newText))
            .thenReturn(Result.failure(testException))

        // ACT: Execute the use case.
        val result = editMessageUseCase(testReceiverId, testMessageId, newText)

        // ASSERT: Verify the interactions and the failure outcome.
        // Ensure the repository's editMessage method was still called.
        verify(mockChatRepository).editMessage(testReceiverId, testMessageId, newText)
        // Ensure the result is a failure.
        assert(result.isFailure)
        // Ensure the correct exception was propagated.
        assertEquals(testException, result.exceptionOrNull())
    }
}