package com.example.splitapp.domain.usecase.group

import com.example.splitapp.domain.repository.GroupRepository

class GetUserNamesUseCase(private val groupRepository: GroupRepository) {
    suspend operator fun invoke(userIds: List<String>): Map<String, String> =
        groupRepository.getUserNames(userIds)
}
