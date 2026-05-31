package com.example.splitapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.User
import com.example.splitapp.data.repository.AuthRepositoryImpl
import com.example.splitapp.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val user: User) : ProfileState()
    data class Error(val message: String) : ProfileState()
}

sealed class UpdateProfileState {
    data object Idle : UpdateProfileState()
    data object Loading : UpdateProfileState()
    data object Success : UpdateProfileState()
    data class Error(val message: String) : UpdateProfileState()
}

sealed class UploadPhotoState {
    data object Idle : UploadPhotoState()
    data object Loading : UploadPhotoState()
    data object Success : UploadPhotoState()
    data class Error(val message: String) : UploadPhotoState()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val profileState: StateFlow<ProfileState> = _profileState

    private val _updateProfileState = MutableStateFlow<UpdateProfileState>(UpdateProfileState.Idle)
    val updateProfileState: StateFlow<UpdateProfileState> = _updateProfileState

    private val _uploadPhotoState = MutableStateFlow<UploadPhotoState>(UploadPhotoState.Idle)
    val uploadPhotoState: StateFlow<UploadPhotoState> = _uploadPhotoState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                val user = authRepository.getCurrentUser()
                if (user != null) {
                    _profileState.value = ProfileState.Success(user)
                } else {
                    _profileState.value = ProfileState.Error("No se pudo cargar el perfil del usuario")
                }
            } catch (e: Exception) {
                _profileState.value = ProfileState.Error("Error: ${e.message}")
            }
        }
    }

    fun updateUserName(newName: String) {
        val sanitized = newName.trim().lowercase()

        if (sanitized.isBlank()) {
            _updateProfileState.value = UpdateProfileState.Error("El nombre no puede estar vacío")
            return
        }
        if (sanitized.length > 15) {
            _updateProfileState.value = UpdateProfileState.Error("Máximo 15 caracteres permitidos")
            return
        }
        if (sanitized.contains(" ")) {
            _updateProfileState.value = UpdateProfileState.Error("El nombre no puede contener espacios")
            return
        }

        viewModelScope.launch {
            _updateProfileState.value = UpdateProfileState.Loading
            try {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                    ?: throw Exception("No hay usuario logueado")

                val result = authRepository.updateUserProfile(uid, sanitized)
                result.onSuccess {
                    _updateProfileState.value = UpdateProfileState.Success
                    loadUserProfile()
                }.onFailure { exception ->
                    _updateProfileState.value = UpdateProfileState.Error(exception.message ?: "Error desconocido")
                }
            } catch (e: Exception) {
                _updateProfileState.value = UpdateProfileState.Error("Error: ${e.message}")
            }
        }
    }

    fun uploadAndUpdatePhoto(uid: String, imageUri: Uri) {
        viewModelScope.launch {
            _uploadPhotoState.value = UploadPhotoState.Loading
            try {
                authRepository.uploadProfilePhoto(uid, imageUri)
                    .onSuccess { photoUrl ->
                        authRepository.updateProfilePhoto(uid, photoUrl)
                            .onSuccess {
                                _uploadPhotoState.value = UploadPhotoState.Success
                                loadUserProfile()
                            }
                            .onFailure { e ->
                                _uploadPhotoState.value = UploadPhotoState.Error(e.message ?: "Error al actualizar")
                            }
                    }
                    .onFailure { e ->
                        _uploadPhotoState.value = UploadPhotoState.Error(e.message ?: "Error al subir")
                    }
            } catch (e: Exception) {
                _uploadPhotoState.value = UploadPhotoState.Error("Error: ${e.message}")
            }
        }
    }

    fun resetUpdateState() {
        _updateProfileState.value = UpdateProfileState.Idle
    }

    fun resetUploadPhotoState() {
        _uploadPhotoState.value = UploadPhotoState.Idle
    }
}
