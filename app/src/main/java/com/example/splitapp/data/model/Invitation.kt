package com.example.splitapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp

data class Invitation(
    @DocumentId val id: String = "",
    val grupoId: String = "",
    val grupoNombre: String = "",
    val invitadoPor: String = "",
    val invitadoPorNombre: String = "",
    val paraUserId: String = "",
    val estado: String = "pendiente",
    val rechazos: Int = 0,
    @ServerTimestamp val createdAt: Timestamp? = null
)
