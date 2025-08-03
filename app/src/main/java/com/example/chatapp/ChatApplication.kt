package com.example.chatapp

import android.app.Application
import com.example.chatapp.di.appModule
import com.example.chatapp.di.domainModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
             androidLogger()
             androidContext(this@ChatApplication)
             modules(appModule,domainModule)
         }
    }
}