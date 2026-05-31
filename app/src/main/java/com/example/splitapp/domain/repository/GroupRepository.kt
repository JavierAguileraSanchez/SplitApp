package com.example.splitapp.domain.repository

import com.example.splitapp.data.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    suspend fun createGroup(nombreGrupo: String, creadorId: String, descripcion: String = ""): Result<Unit>
    fun getGroupsForUser(userId: String): Flow<List<Group>>
    suspend fun addMemberToGroup(groupId: String, email: String): Result<Unit>
    suspend fun addMemberByUid(groupId: String, userId: String)
    suspend fun joinGroupByDeepLink(groupId: String, userId: String): Result<Unit>
    suspend fun searchUsersByName(query: String): Result<List<com.example.splitapp.data.model.User>>
    suspend fun deleteGroup(groupId: String): Result<Unit>
    suspend fun leaveGroup(groupId: String, userId: String): Result<Unit>
    suspend fun getUserNames(userIds: List<String>): Map<String, String>
}
