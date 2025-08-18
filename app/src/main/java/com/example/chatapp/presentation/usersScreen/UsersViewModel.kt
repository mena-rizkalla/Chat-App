package com.example.chatapp.presentation.usersScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.authUseCases.SignOutUseCase
import com.example.chatapp.domain.chatUseCases.GetOnlineUsersUseCase
import com.example.chatapp.domain.chatUseCases.GetUsersUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsersViewModel(
    private val getOnlineUsersUseCase: GetOnlineUsersUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(UsersState())
    val uiState = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UsersEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        _uiState.update { it.copy(isLoading = true) }

        getCurrentUserUseCase()?.let { user ->
            _uiState.update { it.copy(currentUser = user) }
        }

        getOnlineUsersUseCase()
            .onEach { users ->
                _uiState.update {
                    it.copy(users = users, isLoading = false)
                }
            }.launchIn(viewModelScope)
    }

    fun onAction(action: UsersAction) {
        when (action) {
            is UsersAction.OnUserClick -> onUserClick(action.user)
        }
    }

    private fun onUserClick(user: com.example.chatapp.domain.model.User) {
        viewModelScope.launch {
            _eventFlow.emit(UsersEvent.NavigateToChat(receiverId = user.uid, receiverName = user.displayName))
        }
    }
}