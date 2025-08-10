package com.example.chatapp.presentation.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object SignUpScreen : Screen("signup_screen")
    object ForgotPasswordScreen : Screen("forgot_password_screen")
    object UsersScreen : Screen("users_screen")
    object ChatScreen : Screen("chat_screen/{receiverId}/{receiverName}") {
        fun createRoute(receiverId: String, receiverName: String) = "chat_screen/$receiverId/$receiverName"
    }
    object GlobalChatScreen : Screen("global_chat_screen")

    object AiChatScreen : Screen("ai_chat_screen")
    object ProfileScreen : Screen("profile_screen")
    object MainScreen : Screen("main_route")
}
