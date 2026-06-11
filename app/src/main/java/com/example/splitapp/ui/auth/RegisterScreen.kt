package com.example.splitapp.ui.auth

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import android.util.Patterns
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.splitapp.R
import com.example.splitapp.ui.auth.UsernameAvailability
import com.example.splitapp.ui.components.UserAvatar

@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToGroupList: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var nombreSpaceWarning by remember { mutableStateOf(false) }
    var emailSpaceWarning by remember { mutableStateOf(false) }
    var passwordSpaceWarning by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var showPhotoOptions by remember { mutableStateOf(false) }
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val context = LocalContext.current
    val authState by authViewModel.authState.collectAsState()
    val usernameAvailability by authViewModel.usernameAvailability.collectAsState()

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> if (uri != null) photoUri = uri }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success -> if (success) photoUri = pendingCameraUri }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = createTempImageUri(context)
            pendingCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onNavigateToGroupList()
        }
    }

    if (showPhotoOptions) {
        AlertDialog(
            onDismissRequest = { showPhotoOptions = false },
            title = { Text(stringResource(R.string.register_photo_dialog_title)) },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            showPhotoOptions = false
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.choose_from_gallery))
                    }
                    TextButton(
                        onClick = {
                            showPhotoOptions = false
                            val permission = Manifest.permission.CAMERA
                            val granted = androidx.core.content.ContextCompat.checkSelfPermission(
                                context, permission
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            if (granted) {
                                val uri = createTempImageUri(context)
                                pendingCameraUri = uri
                                cameraLauncher.launch(uri)
                            } else {
                                cameraPermissionLauncher.launch(permission)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.take_photo))
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoOptions = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.logo_splitapp),
                contentDescription = stringResource(R.string.login_logo_cd),
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .padding(bottom = 8.dp)
            )

            Text(
                text = stringResource(R.string.register_title),
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clickable { showPhotoOptions = true },
                contentAlignment = Alignment.BottomEnd
            ) {
                UserAvatar(
                    name = nombre.ifBlank { "?" },
                    photoUrl = photoUri?.toString(),
                    size = 96.dp
                )
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = stringResource(R.string.register_add_photo_cd),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { newValue ->
                    nombreSpaceWarning = newValue.contains(" ")
                    val sanitized = newValue.replace(" ", "")
                    if (sanitized.length <= 15) {
                        nombre = sanitized
                        authViewModel.checkUsernameAvailability(sanitized)
                    }
                },
                label = { Text(stringResource(R.string.register_name_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                singleLine = true,
                enabled = authState !is AuthUiState.Loading,
                isError = nombreSpaceWarning || usernameAvailability is UsernameAvailability.Taken,
                supportingText = {
                    val text = when {
                        nombreSpaceWarning -> stringResource(R.string.no_spaces_allowed)
                        usernameAvailability is UsernameAvailability.Taken -> stringResource(R.string.username_taken)
                        usernameAvailability is UsernameAvailability.Available -> stringResource(R.string.username_available)
                        usernameAvailability is UsernameAvailability.Checking -> stringResource(R.string.username_checking)
                        else -> stringResource(R.string.register_name_hint, nombre.length)
                    }
                    val color = when {
                        nombreSpaceWarning || usernameAvailability is UsernameAvailability.Taken ->
                            MaterialTheme.colorScheme.error
                        usernameAvailability is UsernameAvailability.Available ->
                            MaterialTheme.colorScheme.secondary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    Text(text = text, style = MaterialTheme.typography.labelSmall, color = color)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { raw ->
                    emailSpaceWarning = raw.contains(" ")
                    email = raw.replace(" ", "")
                },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                enabled = authState !is AuthUiState.Loading,
                isError = emailSpaceWarning || (email.isNotBlank() && !isEmailValid),
                supportingText = when {
                    emailSpaceWarning -> { { Text(stringResource(R.string.no_spaces_allowed)) } }
                    email.isNotBlank() && !isEmailValid -> { { Text(stringResource(R.string.login_email_error)) } }
                    else -> null
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { raw ->
                    passwordSpaceWarning = raw.contains(" ")
                    password = raw.replace(" ", "")
                },
                label = { Text(stringResource(R.string.login_password_label)) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                enabled = authState !is AuthUiState.Loading,
                isError = passwordSpaceWarning,
                supportingText = if (passwordSpaceWarning) {
                    { Text(stringResource(R.string.no_spaces_allowed)) }
                } else null
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (authState) {
                is AuthUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is AuthUiState.Error -> {
                    Text(
                        text = (authState as AuthUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = {
                            val bytes = photoUri?.let { uri ->
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            }
                            authViewModel.register(nombre, email, password, bytes)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.retry))
                    }
                }

                is AuthUiState.Success -> {
                    CircularProgressIndicator()
                }

                is AuthUiState.Idle -> {
                    Button(
                        onClick = {
                            val bytes = photoUri?.let { uri ->
                                context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                            }
                            authViewModel.register(nombre, email, password, bytes)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEmailValid
                            && usernameAvailability !is UsernameAvailability.Taken
                            && usernameAvailability !is UsernameAvailability.Checking
                    ) {
                        Text(stringResource(R.string.register_button))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.register_login_link),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }

    // Theme toggle — top-left corner
    Row(
        modifier = Modifier
            .align(Alignment.TopStart)
            .statusBarsPadding()
            .padding(top = 8.dp, start = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = { if (isDarkTheme) onThemeChange(false) },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.theme_light),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (!isDarkTheme) FontWeight.Bold else FontWeight.Normal,
                color = if (!isDarkTheme)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        }
        Text(
            text = "|",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        )
        TextButton(
            onClick = { if (!isDarkTheme) onThemeChange(true) },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.theme_dark),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isDarkTheme) FontWeight.Bold else FontWeight.Normal,
                color = if (isDarkTheme)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
            )
        }
    }
    } // end Box
}

private fun createTempImageUri(context: android.content.Context): Uri {
    val tempFile = java.io.File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}
