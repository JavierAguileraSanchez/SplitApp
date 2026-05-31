package com.example.splitapp.domain.usecase.auth

import com.example.splitapp.data.model.User
import com.example.splitapp.domain.repository.AuthRepository

class RegisterUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(nombre: String, email: String, password: String): Result<User> {
        return authRepository.register(nombre, email, password)
    }
}
