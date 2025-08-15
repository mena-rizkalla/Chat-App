package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.User
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

class GetUsersUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var getUsersUseCase: GetUsersUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getUsersUseCase = GetUsersUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return success with list of users when repository succeeds`() = runBlocking {
        val expectedUsers = listOf(
            User(uid = "user1", displayName = "Alice", email = "alice@example.com"),
            User(uid = "user2", displayName = "Bob", email = "bob@example.com")
        )
        whenever(mockChatRepository.getUsers()).thenReturn(Result.success(expectedUsers))


        val result = getUsersUseCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
    }

    @Test
    fun `invoke should return success with an empty list when repository returns no users`() = runBlocking {

        val expectedUsers = emptyList<User>()
        whenever(mockChatRepository.getUsers()).thenReturn(Result.success(expectedUsers))

        val result = getUsersUseCase()

        assertTrue(result.isSuccess)
        assertEquals(expectedUsers, result.getOrNull())
    }

    @Test
    fun `invoke should return failure when repository fails`() = runBlocking {

        val expectedException = RuntimeException("Failed to fetch users")
        whenever(mockChatRepository.getUsers()).thenReturn(Result.failure(expectedException))

        val result = getUsersUseCase()

        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}
