package com.example.splitapp.ui.profile

import com.example.splitapp.data.model.User

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

sealed class UpdatePhoneState {
    data object Idle : UpdatePhoneState()
    data object Loading : UpdatePhoneState()
    data object Success : UpdatePhoneState()
    data class Error(val message: String) : UpdatePhoneState()
}

