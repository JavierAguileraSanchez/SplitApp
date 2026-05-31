package com.example.splitapp.domain.usecase.group

import com.example.splitapp.data.model.Group
import com.example.splitapp.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow

class GetGroupsUseCase(private val groupRepository: GroupRepository) {
    operator fun invoke(userId: String): Flow<List<Group>> {
        return groupRepository.getGroupsForUser(userId)
    }
}
