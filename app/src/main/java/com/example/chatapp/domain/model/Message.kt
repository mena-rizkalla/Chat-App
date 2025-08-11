package com.example.chatapp.domain.model

data class Message(
    val messageId: String = "",
    val senderId: String = "",
    val receiverId: String? = null,
    val text: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val reactions: Map<String, String> = emptyMap(),
)

enum class Reaction(val key: String, val emoji: String) {
    LIKE("like", "👍"),
    LOVE("love", "❤️"),
    WOW("wow", "😮"),
    SAD("sad", "😢"),
    ANGRY("angry", "😠");

    companion object {
        // A helper to find a reaction by its key
        fun fromKey(key: String?): Reaction? = entries.find { it.key == key }
    }
}