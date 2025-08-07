package com.example.chatapp.domain

interface GeminiRepository {
    suspend fun getGeminiResponse(prompt: String): Result<String>
}