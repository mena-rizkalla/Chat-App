package com.example.chatapp.domain

import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    suspend fun getUsers(): Result<List<User>>

    fun getOnlineUsers(): Flow<List<User>>
    suspend fun sendMessage(receiverId: String, text: String,repliedToMessageId: String?, repliedToMessageText: String?, repliedToSenderId: String?): Result<Unit>
    fun getChatMessages(receiverId: String): Flow<List<Message>>
    suspend fun sendGlobalMessage(text: String, repliedToMessageId: String?, repliedToMessageText: String?, repliedToSenderId: String?): Result<Unit>
    fun getGlobalChatMessages(): Flow<List<Message>>

    // New functions for handling reactions
    suspend fun toggleReactionOnPrivateMessage(receiverId: String, messageId: String, reaction: Reaction): Result<Unit>
    suspend fun toggleReactionOnGlobalMessage(messageId: String, reaction: Reaction): Result<Unit>

    // New functions for typing indicator
    fun updateTypingStatus(receiverId: String, isTyping: Boolean)
    fun getTypingStatus(receiverId: String): Flow<Boolean>

    suspend fun updateLastSeenTimestamp(receiverId: String): Result<Unit>
    fun getLastSeenTimestamp(receiverId: String): Flow<Long>

    suspend fun updateUserPresence(): Result<Unit>

}
