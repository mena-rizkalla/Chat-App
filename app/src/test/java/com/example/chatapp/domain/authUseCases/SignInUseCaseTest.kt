package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test

class SignInUseCaseTest {

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var signInUseCase: SignInUseCase

    @BeforeEach
    fun setUp() {
        mockAuthRepository = mock()
        signInUseCase = SignInUseCase(mockAuthRepository)
    }

    @Test
    fun `invoke should call repository signIn and return success`() = runBlocking {
        // Given: Define test data and mock behavior.
        val testEmail = "test@example.com"
        val testPassword = "password123"
        // Configure the mock to return a success result when signIn is called.
        whenever(mockAuthRepository.signIn(testEmail, testPassword)).thenReturn(Result.success(Unit))

        // ACT: Execute the use case.
        val result = signInUseCase(testEmail, testPassword)

        // ASSERT: Verify the interactions and the outcome.
        verify(mockAuthRepository).signIn(testEmail, testPassword)
        // Ensure the result of the use case is a success.
        assert(result.isSuccess)
    }

    @Test
    fun `invoke should call repository signIn and return failure on error`() = runBlocking {
        // Given: Define test data and mock behavior for a failure case.
        val testEmail = "wrong@example.com"
        val testPassword = "wrongpassword"
        val testException = Exception("Authentication failed")
        // Configure the mock to return a failure result.
        whenever(mockAuthRepository.signIn(testEmail, testPassword)).thenReturn(Result.failure(testException))

        // ACT: Execute the use case.
        val result = signInUseCase(testEmail, testPassword)

        // ASSERT: Verify the interactions and the failure outcome.
        verify(mockAuthRepository).signIn(testEmail, testPassword)
        // Ensure the result is a failure.
        assert(result.isFailure)
        // Ensure the correct exception was propagated.
        assertEquals(testException, result.exceptionOrNull())
    }

}