package com.example.chatapp.presentation.usersScreen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.chatapp.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun UsersScreen(navController: NavController, viewModel: UsersViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    // Basic UI for users list
    Text("Users Screen")
    Button(onClick = { navController.navigate(Screen.GlobalChatScreen.route) }) {
        Text("Go to Global Chat")
    }
}