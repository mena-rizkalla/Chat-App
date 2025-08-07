package com.example.chatapp.domain.geminiUseCase

import com.example.chatapp.domain.GeminiRepository

class GetGeminiResponseUseCase(private val repository: GeminiRepository) {
    suspend operator fun invoke(prompt: String): Result<String> {
        return repository.getGeminiResponse(prompt)
    }
}