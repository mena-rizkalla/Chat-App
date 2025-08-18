package com.example.chatapp.domain

import com.example.chatapp.domain.model.User

interface AuthRepository {
    val currentUser: User?
    suspend fun signIn(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut()
}