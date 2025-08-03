package com.example.chatapp.di

import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.authUseCases.SendPasswordResetUseCase
import com.example.chatapp.domain.authUseCases.SignInUseCase
import com.example.chatapp.domain.authUseCases.SignOutUseCase
import com.example.chatapp.domain.authUseCases.SignUpUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetGlobalMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetUsersUseCase
import com.example.chatapp.domain.chatUseCases.SendGlobalMessageUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import org.koin.dsl.module

val domainModule = module {
    // Auth
    factory { GetCurrentUserUseCase(get()) }
    factory { SignInUseCase(get()) }
    factory { SignUpUseCase(get()) }
    factory { SendPasswordResetUseCase(get()) }
    factory { SignOutUseCase(get()) }

    // Chat
    factory { GetUsersUseCase(get()) }
    factory { SendMessageUseCase(get()) }
    factory { GetChatMessagesUseCase(get()) }
    factory { SendGlobalMessageUseCase(get()) }
    factory { GetGlobalMessagesUseCase(get()) }
}