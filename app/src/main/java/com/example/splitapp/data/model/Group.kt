package com.example.splitapp.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp

data class LiquidacionPendiente(
    val confirmaciones: List<String> = emptyList()
)

data class Group(
    @DocumentId val id: String = "",
    val nombreGrupo: String = "",
    val creadoPor: String = "",
    val miembrosActivos: List<String> = emptyList(),
    val estadoMiembros: Map<String, Boolean> = emptyMap(),
    val balancesCentimos: Map<String, Long> = emptyMap(),
    val descripcion: String = "",
    val moneda: String = "EUR",
    val liquidacionPendiente: LiquidacionPendiente? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    @get:Exclude
    val miembros: List<String> get() = estadoMiembros.keys.toList()
}
