package com.example.chatapp.di

import com.example.chatapp.data.repository.AuthRepositoryImpl
import com.example.chatapp.data.repository.ChatRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val appModule = module {

    // Provides a singleton instance of FirebaseAuth
    single { FirebaseAuth.getInstance() }

    // Provides a singleton instance of FirebaseFirestore
    single { FirebaseFirestore.getInstance() }


    single { AuthRepositoryImpl(get(), get()) }


    single { ChatRepositoryImpl(get(), get()) }

}