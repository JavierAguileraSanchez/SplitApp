package com.example.splitapp.domain.repository

import com.example.splitapp.data.model.Invitation
import kotlinx.coroutines.flow.Flow

interface InvitationRepository {
    suspend fun sendInvitation(
        grupoId: String,
        grupoNombre: String,
        invitadoPorId: String,
        invitadoPorNombre: String,
        paraUserId: String
    ): Result<Unit>

    fun getInvitationsForUser(userId: String): Flow<List<Invitation>>

    suspend fun acceptInvitation(invitationId: String, grupoId: String, userId: String): Result<Unit>

    suspend fun rejectInvitation(invitationId: String, grupoId: String, userId: String): Result<Unit>
}
