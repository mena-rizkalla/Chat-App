package com.example.chatapp.di

import com.example.chatapp.presentation.ai_chat.AiChatViewModel
import com.example.chatapp.presentation.chatScreen.ChatViewModel
import com.example.chatapp.presentation.forgetPasswordScreen.ForgotPasswordViewModel
import com.example.chatapp.presentation.globalChatScreen.GlobalChatViewModel
import com.example.chatapp.presentation.loginScreen.LoginViewModel
import com.example.chatapp.presentation.profileScreen.ProfileViewModel
import com.example.chatapp.presentation.signUpScreen.SignUpViewModel
import com.example.chatapp.presentation.usersScreen.UsersViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { LoginViewModel(get(), get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { UsersViewModel(get(), get(), get()) }
    // We pass parameters to ChatViewModel from the navigation graph
    viewModel { params -> ChatViewModel(receiverId = params.get(), receiverName = params.get(),get(), get(), get(), get(), get(), get(), get(),get(),get(),get() ,get(), get()) }
    viewModel { GlobalChatViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { AiChatViewModel(get()) }
    viewModel { ProfileViewModel(get(), get()) }

}