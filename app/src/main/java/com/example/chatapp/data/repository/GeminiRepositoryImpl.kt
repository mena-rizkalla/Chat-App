package com.example.chatapp.data.repository

import android.util.Log
import com.example.chatapp.BuildConfig
import com.example.chatapp.domain.GeminiRepository
import com.google.ai.client.generativeai.GenerativeModel


class GeminiRepositoryImpl : GeminiRepository {
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=$apiKey"

    override suspend fun getGeminiResponse(prompt: String): Result<String> {
        return try {
            val generativeModel = GenerativeModel(
                // Use the standard model name for the SDK
                modelName = "gemini-2.0-flash",
                apiKey = apiKey
            )
            val response = generativeModel.generateContent(prompt)
            Result.success(response.text ?: "No response text found.")
        } catch (e: Exception) {
            // The SDK will throw a more descriptive exception here!
            Log.e("GeminiRepoSDK", "SDK call failed", e)
            Result.failure(e)
        }
    }
}