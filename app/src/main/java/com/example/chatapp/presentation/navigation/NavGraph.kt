package com.example.chatapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.presentation.chatScreen.ChatScreen
import com.example.chatapp.presentation.forgetPasswordScreen.ForgotPasswordScreen
import com.example.chatapp.presentation.globalChatScreen.GlobalChatScreen
import com.example.chatapp.presentation.loginScreen.LoginScreen
import com.example.chatapp.presentation.signUpScreen.SignUpScreen
import com.example.chatapp.presentation.usersScreen.UsersScreen
import org.koin.compose.koinInject

@Composable
fun NavGraph(
    getCurrentUserUseCase: GetCurrentUserUseCase = koinInject()
) {
    val navController = rememberNavController()
    // Determine the start destination based on whether the user is already logged in.
    val startDestination = if (getCurrentUserUseCase() != null) {
        Screen.UsersScreen.route
    } else {
        Screen.LoginScreen.route
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.LoginScreen.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.SignUpScreen.route) {
            SignUpScreen(navController = navController)
        }
        composable(Screen.ForgotPasswordScreen.route) {
            ForgotPasswordScreen(navController = navController)
        }
        composable(Screen.UsersScreen.route) {
            UsersScreen(navController = navController)
        }
        composable(Screen.GlobalChatScreen.route) {
            GlobalChatScreen(navController = navController)
        }
        composable(Screen.ChatScreen.route) { backStackEntry ->
            val receiverId = backStackEntry.arguments?.getString("receiverId") ?: ""
            val receiverName = backStackEntry.arguments?.getString("receiverName") ?: ""
            ChatScreen(
                navController = navController,
                receiverId = receiverId,
                receiverName = receiverName
            )
        }
    }
}