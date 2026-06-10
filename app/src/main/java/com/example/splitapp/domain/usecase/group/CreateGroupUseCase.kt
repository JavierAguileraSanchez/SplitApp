package com.example.splitapp.domain.usecase.group

import com.example.splitapp.domain.repository.GroupRepository

class CreateGroupUseCase(private val groupRepository: GroupRepository) {
    suspend operator fun invoke(nombreGrupo: String, creadorId: String, descripcion: String = "", moneda: String = "EUR"): Result<Unit> {
        return groupRepository.createGroup(nombreGrupo, creadorId, descripcion, moneda)
    }
}
