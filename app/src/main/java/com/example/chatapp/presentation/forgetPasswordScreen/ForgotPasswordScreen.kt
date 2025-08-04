package com.example.chatapp.presentation.forgetPasswordScreen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen(navController: NavController, viewModel: ForgotPasswordViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    Text("Forgot Password Screen")
}