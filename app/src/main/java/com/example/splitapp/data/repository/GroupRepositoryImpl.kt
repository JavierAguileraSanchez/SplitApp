package com.example.splitapp.data.repository

import com.example.splitapp.data.model.Group
import com.example.splitapp.data.model.User
import com.example.splitapp.domain.repository.GroupRepository
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class GroupRepositoryImpl : FirestoreRepository(), GroupRepository {

    override suspend fun createGroup(
        nombreGrupo: String,
        creadorId: String,
        descripcion: String
    ): Result<Unit> = runCatching {
        val newGroup = Group(
            nombreGrupo = nombreGrupo,
            creadoPor = creadorId,
            miembrosActivos = listOf(creadorId),
            estadoMiembros = mapOf(creadorId to true),
            balancesCentimos = mapOf(creadorId to 0L),
            descripcion = descripcion
        )
        withContext(Dispatchers.IO) {
            firestore.collection("grupos").add(newGroup).await()
        }
    }

    override fun getGroupsForUser(userId: String): Flow<List<Group>> = callbackFlow {
        if (userId.isBlank()) {
            trySend(emptyList()); close(); awaitClose { }; return@callbackFlow
        }
        val listener = firestore.collection("grupos")
            .whereArrayContains("miembrosActivos", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) trySend(snapshot.toObjects(Group::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addMemberToGroup(groupId: String, email: String): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                val userSnapshot = firestore.collection("usuarios")
                    .whereEqualTo("email", email.lowercase().trim())
                    .get().await()

                if (userSnapshot.documents.isEmpty()) error("El usuario no está registrado")
                val userId = userSnapshot.documents[0].id
                addMemberByUid(groupId, userId)
            }
        }

    override suspend fun addMemberByUid(groupId: String, userId: String) {
        val groupRef = firestore.collection("grupos").document(groupId)
        firestore.runTransaction { tx ->
            val group = tx.get(groupRef).toObject(Group::class.java)
                ?: error("Grupo no encontrado")
            if (group.estadoMiembros[userId] == true) return@runTransaction

            val nuevoEstado = group.estadoMiembros + (userId to true)
            val nuevosMiembrosActivos = (group.miembrosActivos + userId).distinct()
            val nuevosBalances = if (!group.balancesCentimos.containsKey(userId)) {
                group.balancesCentimos + (userId to 0L)
            } else {
                group.balancesCentimos
            }
            tx.update(groupRef, mapOf(
                "estadoMiembros"   to nuevoEstado,
                "miembrosActivos"  to nuevosMiembrosActivos,
                "balancesCentimos" to nuevosBalances,
                "updatedAt"        to FieldValue.serverTimestamp()
            ))
        }.await()
    }

    override suspend fun joinGroupByDeepLink(groupId: String, userId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            addMemberByUid(groupId, userId)
            val solicitudes = firestore.collection("solicitudes")
                .whereEqualTo("grupoId", groupId)
                .whereEqualTo("paraUserId", userId)
                .get().await()
            for (doc in solicitudes.documents) {
                doc.reference.delete().await()
            }
        }
    }

    override suspend fun searchUsersByName(query: String): Result<List<User>> = runCatching {
        if (query.isBlank()) return@runCatching emptyList()
        val trimmed = query.trim().lowercase()
        val upperBound = trimmed + ""
        withContext(Dispatchers.IO) {
            firestore.collection("usuarios")
                .whereGreaterThanOrEqualTo("nombre", trimmed)
                .whereLessThanOrEqualTo("nombre", upperBound)
                .limit(10)
                .get().await()
                .toObjects(User::class.java)
        }
    }

    override suspend fun deleteGroup(groupId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            firestore.collection("grupos").document(groupId).delete().await()
        }
    }

    override suspend fun getUserNames(userIds: List<String>): Map<String, String> {
        val result = mutableMapOf<String, String>()
        for (id in userIds) {
            try {
                val doc = firestore.collection("usuarios").document(id).get().await()
                val user = doc.toObject(User::class.java)
                result[id] = if (user != null && user.nombre.isNotBlank()) user.nombre else id
            } catch (_: Exception) {
                result[id] = id
            }
        }
        return result
    }

    override suspend fun leaveGroup(groupId: String, userId: String): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val groupRef = firestore.collection("grupos").document(groupId)
            firestore.runTransaction { tx ->
                val group = tx.get(groupRef).toObject(Group::class.java)
                    ?: error("Grupo no encontrado")

                val balance = group.balancesCentimos[userId] ?: 0L
                if (balance != 0L) error("No puedes salir del grupo con saldo pendiente (${balance} ¢)")

                val nuevoEstado = group.estadoMiembros + (userId to false)
                val nuevosMiembrosActivos = group.miembrosActivos - userId
                tx.update(groupRef, mapOf(
                    "estadoMiembros"  to nuevoEstado,
                    "miembrosActivos" to nuevosMiembrosActivos,
                    "updatedAt"       to FieldValue.serverTimestamp()
                ))
            }.await()
        }
    }
}
