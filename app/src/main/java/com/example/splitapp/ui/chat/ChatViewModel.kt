package com.example.splitapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.ChatMessage
import com.example.splitapp.domain.usecase.chat.GetMessagesUseCase
import com.example.splitapp.domain.usecase.chat.SendMessageUseCase
import com.example.splitapp.domain.usecase.group.GetUserNamesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getUserNamesUseCase: GetUserNamesUseCase,
    private val groupId: String,
    val currentUserId: String
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _sending = MutableStateFlow(false)
    val sending: StateFlow<Boolean> = _sending

    private var currentUserName = ""

    init {
        viewModelScope.launch {
            currentUserName = getUserNamesUseCase(listOf(currentUserId))[currentUserId] ?: ""
        }
        viewModelScope.launch {
            getMessagesUseCase(groupId).collect { _messages.value = it }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _sending.value) return
        viewModelScope.launch {
            _sending.value = true
            sendMessageUseCase(groupId, currentUserId, currentUserName, text)
            _sending.value = false
        }
    }
}
