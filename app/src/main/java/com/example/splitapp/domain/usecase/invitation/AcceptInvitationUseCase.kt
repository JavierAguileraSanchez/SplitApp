package com.example.splitapp.domain.usecase.invitation

import com.example.splitapp.domain.repository.InvitationRepository

class AcceptInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(invitationId: String, grupoId: String, userId: String): Result<Unit> =
        repository.acceptInvitation(invitationId, grupoId, userId)
}
