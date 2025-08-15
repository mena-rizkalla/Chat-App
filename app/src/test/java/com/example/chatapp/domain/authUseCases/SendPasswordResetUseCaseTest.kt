package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SendPasswordResetUseCaseTest {

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var sendPasswordResetUseCase: SendPasswordResetUseCase

    @Before
    fun setUp() {
        mockAuthRepository = mock()
        sendPasswordResetUseCase = SendPasswordResetUseCase(mockAuthRepository)
    }

    @Test
    fun `invoke should call repository and return success`() = runBlocking {
        // Given.
        val testEmail = "test@example.com"
        whenever(mockAuthRepository.sendPasswordResetEmail(testEmail)).thenReturn(Result.success(Unit))

        // ACT: Execute the function under test.
        val result = sendPasswordResetUseCase(testEmail)

        // ASSERT: Verify the results.
        verify(mockAuthRepository).sendPasswordResetEmail(testEmail)
        assert(result.isSuccess)
    }

    @Test
    fun `invoke should call repository and return failure on error`() = runBlocking {
        // Given.
        val testEmail = "test@example.com"
        val testException = Exception("Failed to send email")
        whenever(mockAuthRepository.sendPasswordResetEmail(testEmail)).thenReturn(Result.failure(testException))

        // ACT: Execute the function under test.
        val result = sendPasswordResetUseCase(testEmail)

        // ASSERT: Verify the failure results.
        verify(mockAuthRepository).sendPasswordResetEmail(testEmail)
        // Check that the result indicates failure.
        assert(result.isFailure)
        // Check that the exception returned by the use case is the one we defined.
        assertEquals(testException, result.exceptionOrNull())
    }

}