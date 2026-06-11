package com.example.splitapp.data.repository

import com.example.splitapp.data.model.Invitation
import com.example.splitapp.domain.repository.InvitationRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class InvitationRepositoryImpl : InvitationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val invitacionesRef = db.collection("invitaciones")
    private val gruposRef = db.collection("grupos")

    override suspend fun sendInvitation(
        grupoId: String,
        grupoNombre: String,
        invitadoPorId: String,
        invitadoPorNombre: String,
        paraUserId: String
    ): Result<Unit> = runCatching {
        val groupDoc = gruposRef.document(grupoId).get().await()

        @Suppress("UNCHECKED_CAST")
        val bloqueados = groupDoc.get("bloqueados") as? Map<String, Boolean> ?: emptyMap()
        if (bloqueados[paraUserId] == true) error("BLOCKED")

        @Suppress("UNCHECKED_CAST")
        val miembrosActivos = groupDoc.get("miembrosActivos") as? List<String> ?: emptyList()
        if (miembrosActivos.contains(paraUserId)) error("ALREADY_MEMBER")

        val existing = invitacionesRef
            .whereEqualTo("grupoId", grupoId)
            .whereEqualTo("paraUserId", paraUserId)
            .whereEqualTo("invitadoPor", invitadoPorId)
            .whereEqualTo("estado", "pendiente")
            .get().await()
        if (!existing.isEmpty) error("ALREADY_PENDING")

        val invitation = hashMapOf(
            "grupoId" to grupoId,
            "grupoNombre" to grupoNombre,
            "invitadoPor" to invitadoPorId,
            "invitadoPorNombre" to invitadoPorNombre,
            "paraUserId" to paraUserId,
            "estado" to "pendiente",
            "rechazos" to 0,
            "createdAt" to FieldValue.serverTimestamp()
        )
        invitacionesRef.add(invitation).await()
    }

    override fun getInvitationsForUser(userId: String): Flow<List<Invitation>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList()); close(); awaitClose { }; return@callbackFlow
        }
        val listener = invitacionesRef
            .whereEqualTo("paraUserId", userId)
            .whereEqualTo("estado", "pendiente")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) trySend(snapshot.toObjects(Invitation::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun acceptInvitation(
        invitationId: String,
        grupoId: String,
        userId: String
    ): Result<Unit> = runCatching {
        val invRef = invitacionesRef.document(invitationId)
        val groupRef = gruposRef.document(grupoId)

        // Non-members cannot read grupo docs, so we avoid a transaction read.
        // FieldValue operations let us write without knowing current state.
        invRef.update("estado", "aceptada").await()
        groupRef.update(
            mapOf(
                "miembrosActivos" to FieldValue.arrayUnion(userId),
                "estadoMiembros.$userId" to true,
                "balancesCentimos.$userId" to 0L,
                "updatedAt" to FieldValue.serverTimestamp()
            )
        ).await()
    }

    override suspend fun rejectInvitation(
        invitationId: String,
        grupoId: String,
        userId: String
    ): Result<Unit> = runCatching {
        val invRef = invitacionesRef.document(invitationId)
        val groupRef = gruposRef.document(grupoId)

        db.runTransaction { tx ->
            val invSnap = tx.get(invRef)
            val currentRechazos = invSnap.getLong("rechazos")?.toInt() ?: 0
            val newRechazos = currentRechazos + 1

            tx.update(invRef, mapOf(
                "estado" to "rechazada",
                "rechazos" to newRechazos
            ))

            if (newRechazos >= 2) {
                tx.update(groupRef, "bloqueados.$userId", true)
            }
        }.await()
    }
}
