package com.example.chatapp.data.repository

import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
): ChatRepository {

    override suspend fun getUsers(): Result<List<User>> {
        return try {
            val currentUserId = auth.currentUser?.uid
            val snapshot = firestore.collection("users").get().await()
            val users = snapshot.toObjects(User::class.java).filter { it.uid != currentUserId }
            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendMessage(receiverId: String, text: String): Result<Unit> {
        return try {
            val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val messageId = firestore.collection("chats").document().id
            val chatRoomId = getChatRoomId(senderId, receiverId)

            val message = Message(
                messageId = messageId,
                senderId = senderId,
                receiverId = receiverId,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("chats").document(chatRoomId).collection("messages").document(messageId).set(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getChatMessages(receiverId: String): Flow<List<Message>> = callbackFlow {
        val senderId = auth.currentUser?.uid ?: return@callbackFlow
        val chatRoomId = getChatRoomId(senderId, receiverId)

        val subscription = firestore.collection("chats").document(chatRoomId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.toObjects(Message::class.java)
                    trySend(messages).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendGlobalMessage(text: String): Result<Unit> {
        return try {
            val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val messageId = firestore.collection("global_chat").document().id

            val message = Message(
                messageId = messageId,
                senderId = senderId,
                receiverId = null, // No receiver for global messages
                text = text,
                timestamp = System.currentTimeMillis()
            )
            firestore.collection("global_chat").document(messageId).set(message).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getGlobalChatMessages(): Flow<List<Message>> = callbackFlow {
        val subscription = firestore.collection("global_chat")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .limit(100) // Limit to the last 100 messages for performance
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.toObjects(Message::class.java)
                    trySend(messages).isSuccess
                }
            }
        awaitClose { subscription.remove() }
    }


    // Helper to create a consistent chat room ID for any two users.
    private fun getChatRoomId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }
}