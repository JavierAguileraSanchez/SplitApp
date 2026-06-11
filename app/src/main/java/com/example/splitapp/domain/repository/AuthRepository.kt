package com.example.splitapp.domain.repository

import com.example.splitapp.data.model.User

interface AuthRepository {
    suspend fun login(email: String, pass: String): Result<User>
    suspend fun register(nombre: String, email: String, pass: String): Result<User>
    suspend fun getCurrentUser(): User?
    suspend fun updateUserProfile(uid: String, nombre: String): Result<Unit>
    suspend fun uploadProfilePhoto(uid: String, imageBytes: ByteArray): Result<String>
    suspend fun updateProfilePhoto(uid: String, photoUrl: String): Result<Unit>
}
