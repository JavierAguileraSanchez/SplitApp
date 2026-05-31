package com.example.splitapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Expense(
    @DocumentId val id: String = "",
    val concepto: String = "",
    val montoCentimos: Long = 0L,
    val pagadoPor: String = "",
    val createdBy: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    val distribucionCentimos: Map<String, Long> = emptyMap(),
    val clientOperationId: String = "",
    val esPersonalizado: Boolean = false
)
