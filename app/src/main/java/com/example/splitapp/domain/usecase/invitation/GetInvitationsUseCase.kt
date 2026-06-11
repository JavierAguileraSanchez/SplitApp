package com.example.splitapp.domain.usecase.invitation

import com.example.splitapp.data.model.Invitation
import com.example.splitapp.domain.repository.InvitationRepository
import kotlinx.coroutines.flow.Flow

class GetInvitationsUseCase(private val repository: InvitationRepository) {
    operator fun invoke(userId: String): Flow<List<Invitation>> =
        repository.getInvitationsForUser(userId)
}
