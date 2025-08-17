package com.example.chatapp.data.repository

import android.system.Os.close
import com.example.chatapp.domain.ChatRepository
import com.example.chatapp.domain.model.Message
import com.example.chatapp.domain.model.Reaction
import com.example.chatapp.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
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

    override suspend fun sendMessage(
        receiverId: String,
        text: String,
        repliedToMessageId: String?,
        repliedToMessageText: String?,
        repliedToSenderId: String?
    ): Result<Unit> {
        return try {
            val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val messageId = firestore.collection("chats").document().id
            val chatRoomId = getChatRoomId(senderId, receiverId)

            val message = Message(
                messageId = messageId,
                senderId = senderId,
                receiverId = receiverId,
                text = text,
                timestamp = System.currentTimeMillis(),
                repliedToMessageId = repliedToMessageId,
                repliedToMessageText = repliedToMessageText,
                repliedToSenderId = repliedToSenderId
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

    override suspend fun sendGlobalMessage(
        text: String,
        repliedToMessageId: String?,
        repliedToMessageText: String?,
        repliedToSenderId: String?
    ): Result<Unit> {
        return try {
            val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val messageId = firestore.collection("global_chat").document().id

            val message = Message(
                messageId = messageId,
                senderId = senderId,
                receiverId = null, // No receiver for global messages
                text = text,
                timestamp = System.currentTimeMillis(),
                repliedToMessageId = repliedToMessageId,
                repliedToMessageText = repliedToMessageText,
                repliedToSenderId = repliedToSenderId
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

    override suspend fun toggleReactionOnPrivateMessage(
        receiverId: String,
        messageId: String,
        reaction: Reaction
    ): Result<Unit> {
        val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        val chatRoomId = getChatRoomId(senderId, receiverId)
        val messageRef = firestore.collection("chats").document(chatRoomId).collection("messages").document(messageId)

        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(messageRef)
                val currentReactions = snapshot.get("reactions") as? Map<String, String> ?: emptyMap()

                if (currentReactions[senderId] == reaction.key) {
                    transaction.update(messageRef, "reactions.$senderId", FieldValue.delete())
                } else {
                    transaction.update(messageRef, "reactions.$senderId", reaction.key)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleReactionOnGlobalMessage(messageId: String, reaction: Reaction): Result<Unit> {
        val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        val messageRef = firestore.collection("global_chat").document(messageId)

        return try {
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(messageRef)
                val currentReactions = snapshot.get("reactions") as? Map<String, String> ?: emptyMap()

                if (currentReactions[senderId] == reaction.key) {
                    transaction.update(messageRef, "reactions.$senderId", FieldValue.delete())
                } else {
                    transaction.update(messageRef, "reactions.$senderId", reaction.key)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun updateTypingStatus(receiverId: String, isTyping: Boolean) {
        val senderId = auth.currentUser?.uid ?: return
        val chatRoomId = getChatRoomId(senderId, receiverId)
        val status = mapOf(
            "isTyping" to isTyping,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("chats").document(chatRoomId)
            .collection("typing_status").document(senderId)
            .set(status)
    }

    override fun getTypingStatus(receiverId: String): Flow<Boolean> = callbackFlow {
        val senderId = auth.currentUser?.uid ?: return@callbackFlow
        val chatRoomId = getChatRoomId(senderId, receiverId)

        // Listen to the OTHER user's typing status document
        val subscription = firestore.collection("chats").document(chatRoomId)
            .collection("typing_status").document(receiverId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(false)
                    return@addSnapshotListener
                }

                val isTyping = snapshot.getBoolean("isTyping") ?: false
                val timestamp = snapshot.getLong("timestamp") ?: 0

                // Consider not typing if the status is old (e.g., > 5 seconds)
                val isStale = (System.currentTimeMillis() - timestamp) > 5000

                trySend(isTyping && !isStale)
            }

        awaitClose { subscription.remove() }
    }

    override suspend fun updateLastSeenTimestamp(receiverId: String): Result<Unit> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val chatRoomId = getChatRoomId(currentUserId, receiverId)

            val docRef = firestore.collection("chats").document(chatRoomId)
                .collection("read_status").document(currentUserId)

            // use FieldValue.serverTimestamp() for a reliable, server-generated timestamp
            docRef.set(mapOf("lastSeen" to FieldValue.serverTimestamp())).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getLastSeenTimestamp(receiverId: String): Flow<Long>  = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?:  return@callbackFlow
        val chatRoomId = getChatRoomId(currentUserId, receiverId)

        // listen to the OTHER user's (the receiver's) document
        val docRef = firestore.collection("chats").document(chatRoomId)
            .collection("read_status").document(receiverId)

        val subscription = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error) // Close the flow on error
                return@addSnapshotListener
            }
            // Convert the Firestore Timestamp to a simple Long (milliseconds)
            val timestamp = snapshot?.getTimestamp("lastSeen")?.toDate()?.time ?: 0L
            trySend(timestamp)
        }

        awaitClose { subscription.remove() }
    }

    override fun getOnlineUsers(): Flow<List<User>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid
        val usersCollection = firestore.collection("users")

        val listener = usersCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val users = snapshot.toObjects(User::class.java).filter { it.uid != currentUserId }
                trySend(users)
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateUserPresence(): Result<Unit> {
        val currentUserId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
        return try {
            firestore.collection("users").document(currentUserId)
                .update("lastSeenTimestamp", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun editMessage(
        receiverId: String,
        messageId: String,
        newText: String
    ): Result<Unit> {
       return try {
           val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
           val chatRoomId = getChatRoomId(senderId, receiverId)
           firestore.collection("chats").document(chatRoomId)
               .collection("messages").document(messageId)
               .update(mapOf("text" to newText, "isEdited" to true))
               .await()
           Result.success(Unit)
       } catch (e: Exception) {
           Result.failure(e)
       }
    }

    override suspend fun deleteMessage(
        receiverId: String,
        messageId: String
    ): Result<Unit> {
        return try {
            val senderId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))
            val chatRoomId = getChatRoomId(senderId, receiverId)
            firestore.collection("chats").document(chatRoomId)
                .collection("messages").document(messageId)
                .delete()
                .await()
            Result.success(Unit)
        }catch (e: Exception){
            Result.failure(e)
        }
    }

    override fun getUserProfileStream(uid: String): Flow<User?> = callbackFlow {
        val docRef = firestore.collection("users").document(uid)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            trySend(snapshot?.toObject(User::class.java))
        }
        awaitClose { listener.remove() }
    }



    // Helper to create a consistent chat room ID for any two users.
    private fun getChatRoomId(user1: String, user2: String): String {
        return if (user1 < user2) "$user1-$user2" else "$user2-$user1"
    }
}