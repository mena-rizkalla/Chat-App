package com.example.chatapp.presentation.loginScreen

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import com.example.chatapp.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    // Basic UI for login - you would replace this with a proper design
    Text("Login Screen")
    if (uiState.isSignedIn) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.UsersScreen.route)
            { popUpTo(Screen.LoginScreen.route) { inclusive = true } }
        }
    }
}