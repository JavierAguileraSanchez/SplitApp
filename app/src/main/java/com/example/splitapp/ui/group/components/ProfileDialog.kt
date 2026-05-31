package com.example.splitapp.ui.group.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.splitapp.ui.components.UserAvatar
import com.example.splitapp.ui.profile.ProfileState
import com.example.splitapp.ui.profile.UpdateProfileState
import com.example.splitapp.ui.profile.UploadPhotoState

@Composable
fun ProfileDialog(
    profileState: ProfileState,
    updateProfileState: UpdateProfileState,
    uploadPhotoState: UploadPhotoState,
    profileNameInput: String,
    onProfileNameChange: (String) -> Unit,
    onSave: () -> Unit,
    onUploadPhoto: (Uri) -> Unit,
    onResetUploadPhoto: () -> Unit,
    onDismiss: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    var showPhotoOptions by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> uri?.let { onUploadPhoto(it) } }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) pendingCameraUri?.let { onUploadPhoto(it) } }

    LaunchedEffect(uploadPhotoState) {
        if (uploadPhotoState is UploadPhotoState.Success) onResetUploadPhoto()
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text("Cambiar foto de perfil") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            showPhotoOptions = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Photo, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Elegir de la galería")
                    }
                    TextButton(
                        onClick = {
                            showPhotoOptions = false
                            val uri = createTempImageUri(context)
                            pendingCameraUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.PhotoCamera, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar foto")
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false }) { Text("Cancelar") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Perfil") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                when (profileState) {
                    is ProfileState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                    }
                    is ProfileState.Success -> {
                        val user = profileState.user

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UserAvatar(
                                name = user.nombre,
                                photoUrl = user.photoUrl.takeIf { it.isNotBlank() },
                                size = 80.dp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = { showPhotoOptions = true },
                                enabled = uploadPhotoState !is UploadPhotoState.Loading
                            ) {
                                Text("Cambiar foto")
                            }
                            if (uploadPhotoState is UploadPhotoState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            }
                            if (uploadPhotoState is UploadPhotoState.Error) {
                                Text(
                                    text = uploadPhotoState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Nombre", style = MaterialTheme.typography.labelMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = profileNameInput,
                            onValueChange = onProfileNameChange,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = updateProfileState !is UpdateProfileState.Loading,
                            supportingText = {
                                Text(
                                    text = "Sin espacios · ${profileNameInput.length}/15 caracteres",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Email: ${user.email}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Rol: ${if (user.role == "admin") "Administrador" else "Usuario"}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        if (updateProfileState is UpdateProfileState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                        }
                        if (updateProfileState is UpdateProfileState.Error) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = updateProfileState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        if (updateProfileState is UpdateProfileState.Success) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Perfil actualizado correctamente",
                                color = MaterialTheme.colorScheme.secondary,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    is ProfileState.Error -> {
                        Text(text = profileState.message, color = MaterialTheme.colorScheme.error)
                    }
                    is ProfileState.Idle -> {
                        Text("No se pudo cargar el perfil")
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = updateProfileState !is UpdateProfileState.Loading
            ) {
                Text("Guardar cambios")
            }
        },
        dismissButton = {
            Column {
                TextButton(onClick = onDismiss) { Text("Cerrar") }
                TextButton(
                    onClick = onLogout,
                    enabled = updateProfileState !is UpdateProfileState.Loading
                ) {
                    Text("Cerrar Sesión")
                }
            }
        }
    )
}

private fun createTempImageUri(context: android.content.Context): Uri {
    val tempFile = java.io.File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
