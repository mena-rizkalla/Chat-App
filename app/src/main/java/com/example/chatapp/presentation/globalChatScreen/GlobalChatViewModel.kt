package com.example.chatapp.presentation.globalChatScreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.chatUseCases.GetGlobalMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetUsersUseCase
import com.example.chatapp.domain.chatUseCases.SendGlobalMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class GlobalChatViewModel(
    private val getGlobalMessagesUseCase: GetGlobalMessagesUseCase,
    private val sendGlobalMessageUseCase: SendGlobalMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getUsersUseCase: GetUsersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GlobalChatState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 1. Set loading state and get current user ID
            val currentUserId = getCurrentUserUseCase()?.uid ?: ""
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                currentUserId = currentUserId
            )
            Log.d("GlobalChatViewModel", "Current User ID set to: $currentUserId")


            // 2. Fetch the list of users first.
            getUsersUseCase().onSuccess { users ->
                val userMap = users.associateBy({ it.uid }, { it.displayName })
                Log.d("GlobalChatViewModel", "User Map Loaded: $userMap")

                // 3. If users are fetched successfully, start listening for messages.
                getGlobalMessagesUseCase().onEach { messages ->
                    val uiMessages = messages.map { message ->
                        UiMessage(
                            message = message,
                            senderDisplayName = userMap[message.senderId] ?: "Unknown User"
                        )
                    }
                    _uiState.value = _uiState.value.copy(
                        messages = uiMessages,
                        isLoading = false // Turn off loading once we have messages
                    )
                }.catch { e ->
                    Log.e("GlobalChatViewModel", "Error listening for messages", e)
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }.launchIn(viewModelScope)

            }.onFailure { e ->
                Log.e("GlobalChatViewModel", "Error fetching users", e)
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(currentMessage = message)
    }

    fun sendMessage() {
        viewModelScope.launch {
            val text = uiState.value.currentMessage
            if (text.isNotBlank()) {
                sendGlobalMessageUseCase(text)
                _uiState.value = _uiState.value.copy(currentMessage = "") // Clear input
            }
        }
    }
}