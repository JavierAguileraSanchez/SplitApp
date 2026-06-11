package com.example.splitapp.domain.usecase.chat

import com.example.splitapp.domain.repository.ChatRepository

class SendMessageUseCase(private val chatRepository: ChatRepository) {
    suspend operator fun invoke(
        groupId: String,
        userId: String,
        userName: String,
        text: String
    ): Result<Unit> = chatRepository.sendMessage(groupId, userId, userName, text)
}
