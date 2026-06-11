package com.example.splitapp.domain.repository

import com.example.splitapp.data.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(groupId: String): Flow<List<ChatMessage>>
    suspend fun sendMessage(groupId: String, userId: String, userName: String, text: String): Result<Unit>
}
