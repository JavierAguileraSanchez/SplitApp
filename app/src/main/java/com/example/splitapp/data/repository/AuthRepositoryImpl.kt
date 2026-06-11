package com.example.splitapp.data.repository

import com.example.splitapp.data.model.User
import com.example.splitapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl : FirestoreRepository(), AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()

    override suspend fun login(email: String, pass: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("UID no disponible")
            val userDoc = firestore.collection("usuarios").document(uid).get().await()
            val user = userDoc.toObject(User::class.java) ?: throw Exception("Usuario no encontrado en Firestore")
            Result.success(user)
        } catch (e: Exception) {
            val mensaje = when {
                e.message?.contains("no user record") == true -> "El email no está registrado"
                e.message?.contains("password is invalid") == true -> "Contraseña incorrecta"
                else -> "Error al iniciar sesión: ${e.message}"
            }
            Result.failure(Exception(mensaje))
        }
    }

    override suspend fun register(nombre: String, email: String, pass: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            val uid = authResult.user?.uid ?: throw Exception("UID no disponible")

            val newUser = User(
                id = uid,
                nombre = nombre.lowercase().trim(),
                email = email,
                role = "user"
            )

            firestore.collection("usuarios").document(uid).set(newUser).await()

            Result.success(newUser)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("El email ya está registrado"))
        } catch (e: Exception) {
            Result.failure(Exception("Error al registrarse: ${e.message}"))
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val uid = firebaseAuth.currentUser?.uid ?: return null
            val userDoc = firestore.collection("usuarios").document(uid).get().await()
            val user = userDoc.toObject(User::class.java)
            // Ensure role is properly read from Firestore
            if (user != null && user.role.isBlank()) {
                user.copy(role = "user")
            } else {
                user
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateUserProfile(uid: String, nombre: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                firestore.collection("usuarios").document(uid).update(
                    mapOf("nombre" to nombre.lowercase().trim())
                ).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar perfil: ${e.message}"))
        }
    }

    override suspend fun uploadProfilePhoto(uid: String, imageBytes: ByteArray): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val storageRef = FirebaseStorage.getInstance().reference
                    .child("profile_photos/$uid.jpg")
                storageRef.putBytes(imageBytes).await()
                val downloadUrl = storageRef.downloadUrl.await().toString()
                Result.success(downloadUrl)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Error al subir foto: ${e.message}"))
        }
    }

    override suspend fun updateProfilePhoto(uid: String, photoUrl: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                firestore.collection("usuarios").document(uid)
                    .update("photoUrl", photoUrl).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Error al actualizar foto: ${e.message}"))
        }
    }
}
