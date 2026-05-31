package com.example.splitapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class Group(
    @DocumentId val id: String = "",
    val nombreGrupo: String = "",
    val creadoPor: String = "",
    // Array auxiliar para whereArrayContains en Firestore
    val miembrosActivos: List<String> = emptyList(),
    // Fuente de verdad del estado de cada miembro (uid → isActive)
    val estadoMiembros: Map<String, Boolean> = emptyMap(),
    val balancesCentimos: Map<String, Long> = emptyMap(),
    val descripcion: String = "",
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    @get:Exclude
    val miembros: List<String> get() = estadoMiembros.keys.toList()
}
