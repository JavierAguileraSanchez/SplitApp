package com.example.splitapp.domain.usecase.invitation

import com.example.splitapp.domain.repository.InvitationRepository

class SendInvitationUseCase(private val repository: InvitationRepository) {
    suspend operator fun invoke(
        grupoId: String,
        grupoNombre: String,
        invitadoPorId: String,
        invitadoPorNombre: String,
        paraUserId: String
    ): Result<Unit> = repository.sendInvitation(grupoId, grupoNombre, invitadoPorId, invitadoPorNombre, paraUserId)
}
