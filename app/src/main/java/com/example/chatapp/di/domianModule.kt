package com.example.chatapp.di

import com.example.chatapp.domain.authUseCases.GetCurrentUserUseCase
import com.example.chatapp.domain.authUseCases.SendPasswordResetUseCase
import com.example.chatapp.domain.authUseCases.SignInUseCase
import com.example.chatapp.domain.authUseCases.SignOutUseCase
import com.example.chatapp.domain.authUseCases.SignUpUseCase
import com.example.chatapp.domain.chatUseCases.GetChatMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetGlobalMessagesUseCase
import com.example.chatapp.domain.chatUseCases.GetTypingStatusUseCase
import com.example.chatapp.domain.chatUseCases.GetUsersUseCase
import com.example.chatapp.domain.chatUseCases.MarkMessageAsReadUseCase
import com.example.chatapp.domain.chatUseCases.SendGlobalMessageUseCase
import com.example.chatapp.domain.chatUseCases.SendMessageUseCase
import com.example.chatapp.domain.chatUseCases.ToggleGlobalMessageReactionUseCase
import com.example.chatapp.domain.chatUseCases.TogglePrivateMessageReactionUseCase
import com.example.chatapp.domain.chatUseCases.UpdateTypingStatusUseCase
import com.example.chatapp.domain.geminiUseCase.GetGeminiResponseUseCase
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

    //ai chat
    factory { GetGeminiResponseUseCase(get()) }

    // Reactions
    factory { TogglePrivateMessageReactionUseCase(get()) }
    factory { ToggleGlobalMessageReactionUseCase(get()) }

    // Typing status
    factory { UpdateTypingStatusUseCase(get()) }
    factory { GetTypingStatusUseCase(get()) }

    factory { MarkMessageAsReadUseCase(get()) }

}