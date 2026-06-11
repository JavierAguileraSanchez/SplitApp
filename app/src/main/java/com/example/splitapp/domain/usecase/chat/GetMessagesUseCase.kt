package com.example.splitapp.domain.usecase.chat

import com.example.splitapp.data.model.ChatMessage
import com.example.splitapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class GetMessagesUseCase(private val chatRepository: ChatRepository) {
    operator fun invoke(groupId: String): Flow<List<ChatMessage>> =
        chatRepository.getMessages(groupId)
}
