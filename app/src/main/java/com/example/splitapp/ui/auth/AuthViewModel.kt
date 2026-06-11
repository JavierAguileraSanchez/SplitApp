package com.example.splitapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.User
import com.example.splitapp.data.repository.AuthRepositoryImpl
import com.example.splitapp.domain.repository.AuthRepository
import com.example.splitapp.domain.usecase.auth.LoginUseCase
import com.example.splitapp.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UsernameAvailability {
    data object Idle : UsernameAvailability()
    data object Checking : UsernameAvailability()
    data object Available : UsernameAvailability()
    data object Taken : UsernameAvailability()
}

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

    private val _usernameAvailability = MutableStateFlow<UsernameAvailability>(UsernameAvailability.Idle)
    val usernameAvailability: StateFlow<UsernameAvailability> = _usernameAvailability

    private var usernameCheckJob: Job? = null

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

    fun checkUsernameAvailability(nombre: String) {
        usernameCheckJob?.cancel()
        if (nombre.isBlank()) { _usernameAvailability.value = UsernameAvailability.Idle; return }
        _usernameAvailability.value = UsernameAvailability.Checking
        usernameCheckJob = viewModelScope.launch {
            delay(500L)
            authRepository.isUsernameAvailable(nombre)
                .onSuccess { available ->
                    _usernameAvailability.value =
                        if (available) UsernameAvailability.Available else UsernameAvailability.Taken
                }
                .onFailure { _usernameAvailability.value = UsernameAvailability.Idle }
        }
    }

    fun resetUsernameAvailability() { _usernameAvailability.value = UsernameAvailability.Idle }

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
