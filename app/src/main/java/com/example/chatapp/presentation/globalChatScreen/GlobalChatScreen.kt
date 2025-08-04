package com.example.chatapp.presentation.globalChatScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun GlobalChatScreen(navController: NavController, viewModel: GlobalChatViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Text("Global Chat")
}