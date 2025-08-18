package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
class SignOutUseCaseTest {

    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var signOutUseCase: SignOutUseCase

    @BeforeEach
    fun setUp() {
        mockAuthRepository = mock()
        signOutUseCase = SignOutUseCase(mockAuthRepository)
    }

    @Test
    fun `invoke should call repository signOut`() = runBlocking {
        // ACT: Execute the use case.
        signOutUseCase()

        // ASSERT: Verify that the repository's signOut method was called exactly once.
        verify(mockAuthRepository).signOut()
    }

    @Test
    fun `invoke should propagate exception when repository throws error`() = runBlocking {
        // Given
        val testException = RuntimeException("Sign out failed")
        doThrow(testException).`when`(mockAuthRepository).signOut()

        // ACT & ASSERT
        val thrownException = assertThrows<RuntimeException> {
            signOutUseCase()
        }

        // You can now make more assertions on the exception itself
        assertEquals("Sign out failed", thrownException.message)
    }
}