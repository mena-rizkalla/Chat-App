package com.example.chatapp.presentation.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatapp.presentation.ai_chat.AiChatScreen
import com.example.chatapp.presentation.globalChatScreen.GlobalChatScreen
import com.example.chatapp.presentation.navigation.Screen
import com.example.chatapp.presentation.profileScreen.ProfileScreen
import com.example.chatapp.presentation.usersScreen.UsersScreen

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(mainNavController: NavController) {
    val bottomNavController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController = bottomNavController) }
    ) { innerPadding ->
        NavHost(navController = bottomNavController,
            startDestination = Screen.UsersScreen.route,
            modifier = Modifier.padding(PaddingValues(bottom = innerPadding.calculateBottomPadding() - 18.dp))
        ) {
            composable(Screen.UsersScreen.route) {
                UsersScreen(navController = mainNavController)
            }
            composable(Screen.GlobalChatScreen.route) {
                GlobalChatScreen(navController = bottomNavController)
            }
            composable(Screen.AiChatScreen.route) {
                AiChatScreen(navController = bottomNavController)
            }
            composable(Screen.ProfileScreen.route) {
                ProfileScreen(navController = mainNavController)
            }
        }
    }
}
