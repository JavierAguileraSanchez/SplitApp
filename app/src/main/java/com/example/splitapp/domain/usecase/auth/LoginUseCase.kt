package com.example.splitapp.domain.usecase.auth

import com.example.splitapp.data.model.User
import com.example.splitapp.domain.repository.AuthRepository

class LoginUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        return authRepository.login(email, password)
    }
}
