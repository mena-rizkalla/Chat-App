package com.example.chatapp.presentation.chatScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun ChatScreen(
    navController: NavController,
    receiverId: String,
    receiverName: String,
    viewModel: ChatViewModel = koinViewModel(parameters = { org.koin.core.parameter.parametersOf(receiverId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    // Basic UI for chat
    Text("Chat with $receiverName")
}