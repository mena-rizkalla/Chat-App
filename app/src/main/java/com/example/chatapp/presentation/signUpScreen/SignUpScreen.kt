package com.example.chatapp.presentation.signUpScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.example.chatapp.presentation.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignUpScreen(navController: NavController, viewModel: SignUpViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Text("Sign Up Screen")
    if (uiState.isSignedUp) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.UsersScreen.route) { popUpTo(Screen.LoginScreen.route) { inclusive = true } }
        }
    }
}