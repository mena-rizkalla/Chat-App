package com.example.chatapp.di

import com.example.chatapp.data.repository.AuthRepositoryImpl
import com.example.chatapp.data.repository.ChatRepositoryImpl
import com.example.chatapp.data.repository.GeminiRepositoryImpl
import com.example.chatapp.domain.AuthRepository
import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.GeminiRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val appModule = module {

    // Provides a singleton instance of FirebaseAuth
    single { FirebaseAuth.getInstance() }

    // Provides a singleton instance of FirebaseFirestore
    single { FirebaseFirestore.getInstance() }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // Binds the ChatRepository interface to its implementation
    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }

    single<GeminiRepository> { GeminiRepositoryImpl() }

}