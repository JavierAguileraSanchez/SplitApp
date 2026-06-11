package com.example.splitapp.data.repository

import com.example.splitapp.data.model.ChatMessage
import com.example.splitapp.domain.repository.ChatRepository
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChatRepositoryImpl : FirestoreRepository(), ChatRepository {

    override fun getMessages(groupId: String): Flow<List<ChatMessage>> = callbackFlow {
        val listener = firestore.collection("grupos").document(groupId)
            .collection("mensajes")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) trySend(snapshot.toObjects(ChatMessage::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun sendMessage(
        groupId: String,
        userId: String,
        userName: String,
        text: String
    ): Result<Unit> = runCatching {
        val message = ChatMessage(userId = userId, userName = userName, text = text.trim())
        firestore.collection("grupos").document(groupId)
            .collection("mensajes")
            .add(message)
            .await()
        Unit
    }
}
