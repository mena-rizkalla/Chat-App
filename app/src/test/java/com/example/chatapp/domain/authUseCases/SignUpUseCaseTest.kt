package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test
import kotlin.test.assertEquals


class SignUpUseCaseTest {

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var signUpUseCase: SignUpUseCase

    @BeforeEach
    fun setUp() {
        mockAuthRepository = mock()
        signUpUseCase = SignUpUseCase(mockAuthRepository)
    }

    @Test
    fun `invoke should call repository signUp and return success`() = runBlocking {
        // Given: Define test data and mock behavior.
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val testDisplayName = "Test User"
        whenever(mockAuthRepository.signUp(testEmail, testPassword, testDisplayName))
            .thenReturn(Result.success(Unit))

        // ACT: Execute the use case.
        val result = signUpUseCase(testEmail, testPassword, testDisplayName)

        // ASSERT: Verify the interactions and the outcome.
        verify(mockAuthRepository).signUp(testEmail, testPassword, testDisplayName)
        // Ensure the result of the use case is a success.
        assert(result.isSuccess)
    }

    @Test
    fun `invoke should call repository signUp and return failure on error`() = runBlocking {
        // Given: Define test data and mock behavior for a failure case.
        val testEmail = "test@example.com"
        val testPassword = "password123"
        val testDisplayName = "Test User"
        val testException = Exception("Email already in use")
        // Configure the mock to return a failure result.
        whenever(mockAuthRepository.signUp(testEmail, testPassword, testDisplayName))
            .thenReturn(Result.failure(testException))

        // ACT: Execute the use case.
        val result = signUpUseCase(testEmail, testPassword, testDisplayName)

        // ASSERT: Verify the interactions and the failure outcome.
        verify(mockAuthRepository).signUp(testEmail, testPassword, testDisplayName)
        // Ensure the result is a failure.
        assert(result.isFailure)
        // Ensure the correct exception was propagated.
        assertEquals(testException, result.exceptionOrNull())
    }

}