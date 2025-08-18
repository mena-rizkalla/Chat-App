package com.example.chatapp.domain.authUseCases

import com.example.chatapp.domain.AuthRepository
import com.example.chatapp.domain.model.User
import junit.framework.TestCase.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.Test


class GetCurrentUserUseCaseTest {
    private lateinit var mockAuthRepository: AuthRepository
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    private val mockFirebaseUser: User? = mock()

    @BeforeEach
    fun setUp() {
        mockAuthRepository = mock()
        getCurrentUserUseCase = GetCurrentUserUseCase(mockAuthRepository)
    }

    @Test
    fun `invoke should return current user from repository`() {
        // When 'currentUser' property on mockAuthRepository is accessed,
        whenever(mockAuthRepository.currentUser).thenReturn(mockFirebaseUser)

        // ACT: Execute the code we want to test.
        val result = getCurrentUserUseCase()

        // ASSERT: Verify that the outcome is what we expected.
        assertEquals(mockFirebaseUser, result)

        // Verify that the 'currentUser' property on the repository
        // was accessed exactly one time.
        verify(mockAuthRepository).currentUser
    }

    @Test
    fun `invoke should return null when no user is logged in`() {
        // ARRANGE: This time, we tell the mock to return null.
        whenever(mockAuthRepository.currentUser).thenReturn(null)

        // ACT: Execute the use case.
        val result = getCurrentUserUseCase()

        // ASSERT: Verify that the result is null.
        assertEquals(null, result)
        verify(mockAuthRepository).currentUser
    }

}