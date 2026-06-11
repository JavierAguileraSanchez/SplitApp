package com.example.splitapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.User
import com.example.splitapp.data.repository.AuthRepositoryImpl
import com.example.splitapp.domain.repository.AuthRepository
import com.example.splitapp.domain.usecase.auth.LoginUseCase
import com.example.splitapp.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: User) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authState: StateFlow<AuthUiState> = _authState

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val result = loginUseCase(email, password)
                result.onSuccess { user ->
                    _authState.value = AuthUiState.Success(user)
                }.onFailure { exception ->
                    _authState.value = AuthUiState.Error(exception.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Error al iniciar sesión: ${e.message}")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthUiState.Idle
    }

    fun register(nombre: String, email: String, password: String, photoBytes: ByteArray? = null) {
        viewModelScope.launch {
            _authState.value = AuthUiState.Loading
            try {
                val result = registerUseCase(nombre, email, password)
                result.onSuccess { user ->
                    if (photoBytes != null) {
                        try {
                            authRepository.uploadProfilePhoto(user.id, photoBytes)
                                .onSuccess { url -> authRepository.updateProfilePhoto(user.id, url) }
                        } catch (_: Exception) {
                            // La subida de foto no bloquea el registro
                        }
                    }
                    _authState.value = AuthUiState.Success(user)
                }.onFailure { exception ->
                    _authState.value = AuthUiState.Error(exception.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _authState.value = AuthUiState.Error("Error al registrarse: ${e.message}")
            }
        }
    }
}
