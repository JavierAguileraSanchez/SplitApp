package com.example.splitapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class ChatMessage(
    @DocumentId val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val text: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null
)
