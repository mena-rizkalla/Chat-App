package com.example.chatapp.domain.chatUseCases

import com.example.chatapp.domain.ChatRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

class GetLastSeenUseCaseTest {

    @Mock
    private lateinit var mockChatRepository: ChatRepository

    private lateinit var getLastSeenUseCase: GetLastSeenUseCase

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        getLastSeenUseCase = GetLastSeenUseCase(mockChatRepository)
    }

    @Test
    fun `invoke should return flow of timestamp when repository succeeds`() = runBlocking {

        val dummyReceiverId = "user2"
        val expectedTimestamp = System.currentTimeMillis()
        whenever(mockChatRepository.getLastSeenTimestamp(any())).thenReturn(flowOf(expectedTimestamp))

        val timestampFlow = getLastSeenUseCase(dummyReceiverId)
        val actualTimestamp = timestampFlow.first()

        assertEquals(expectedTimestamp, actualTimestamp)
    }

    @Test
    fun `invoke should return 0L when no timestamp is available`() = runBlocking {

        val dummyReceiverId = "user3"
        val expectedTimestamp = 0L
        whenever(mockChatRepository.getLastSeenTimestamp(any())).thenReturn(flowOf(expectedTimestamp))

        val timestampFlow = getLastSeenUseCase(dummyReceiverId)
        val actualTimestamp = timestampFlow.first()

        assertEquals(expectedTimestamp, actualTimestamp)
    }

    @Test
    fun `invoke should propagate error when repository fails`() {

        val dummyReceiverId = "user4"
        val expectedException = RuntimeException("Database error")
        val errorFlow = flow<Long> { throw expectedException }
        whenever(mockChatRepository.getLastSeenTimestamp(any())).thenReturn(errorFlow)

        val exception = assertThrows(RuntimeException::class.java) {
            runBlocking {
                getLastSeenUseCase(dummyReceiverId).first()
            }
        }
        assertEquals("Database error", exception.message)
    }
}