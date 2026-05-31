package com.example.splitapp.domain.usecase.group

import com.example.splitapp.domain.repository.GroupRepository

class AddMemberUseCase(private val groupRepository: GroupRepository) {
    suspend operator fun invoke(groupId: String, email: String): Result<Unit> {
        return groupRepository.addMemberToGroup(groupId, email)
    }
}
