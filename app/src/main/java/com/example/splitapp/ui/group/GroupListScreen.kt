package com.example.splitapp.ui.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import com.example.splitapp.util.formatEuros
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitapp.data.model.Group
import com.example.splitapp.ui.profile.ProfileViewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.splitapp.ui.components.UserAvatar
import com.example.splitapp.ui.profile.UploadPhotoState
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    groupViewModel: GroupViewModel,
    onNavigateToGroupDetail: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    deepLinkGroupId: String? = null
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var profileNameInput by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }
    var joinLinkInput by remember { mutableStateOf("") }
    var joinError by remember { mutableStateOf("") }

    var groupForAction by remember { mutableStateOf<Group?>(null) }

    val profileViewModel: ProfileViewModel = viewModel()
    val profileState by profileViewModel.profileState.collectAsState()
    val updateProfileState by profileViewModel.updateProfileState.collectAsState()
    val uploadPhotoState by profileViewModel.uploadPhotoState.collectAsState()

    var showPhotoOptionsDialog by remember { mutableStateOf(false) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@let
            profileViewModel.uploadAndUpdatePhoto(uid, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            pendingCameraUri?.let { uri ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@let
                profileViewModel.uploadAndUpdatePhoto(uid, uri)
            }
        }
    }

    val groups by groupViewModel.groups.collectAsState()
    val globalBalanceState by groupViewModel.globalBalanceState.collectAsState()
    val createGroupState by groupViewModel.createGroupState.collectAsState()
    val groupActionState by groupViewModel.groupActionState.collectAsState()
    val joinGroupState by groupViewModel.joinGroupState.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    LaunchedEffect(deepLinkGroupId) {
        if (!deepLinkGroupId.isNullOrBlank()) {
            joinLinkInput = deepLinkGroupId
            showJoinDialog = true
        }
    }

    LaunchedEffect(groupActionState) {
        if (groupActionState is GroupActionState.Success) {
            groupForAction = null
            groupViewModel.resetGroupActionState()
        }
    }

    LaunchedEffect(joinGroupState) {
        if (joinGroupState is JoinGroupState.Success) {
            showJoinDialog = false
            joinLinkInput = ""
            joinError = ""
            groupViewModel.resetJoinGroupState()
        }
    }

    LaunchedEffect(createGroupState) {
        if (createGroupState is CreateGroupState.Success) {
            showCreateDialog = false
            groupViewModel.resetCreateGroupState()
        }
    }

    LaunchedEffect(profileState) {
        if (profileState is com.example.splitapp.ui.profile.ProfileState.Success) {
            profileNameInput = (profileState as com.example.splitapp.ui.profile.ProfileState.Success).user.nombre
            profileError = ""
        }
    }

    LaunchedEffect(uploadPhotoState) {
        if (uploadPhotoState is UploadPhotoState.Success) {
            profileViewModel.resetUploadPhotoState()
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = {
                showCreateDialog = false
                groupName = ""
                groupDescription = ""
                groupViewModel.resetCreateGroupState()
            },
            title = { Text("Crear Nuevo Grupo") },
            text = {
                Column {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text("Nombre del grupo") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = createGroupState !is CreateGroupState.Loading
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = groupDescription,
                        onValueChange = { groupDescription = it },
                        label = { Text("Descripción (opcional)") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = createGroupState !is CreateGroupState.Loading,
                        minLines = 2
                    )
                    if (createGroupState is CreateGroupState.Error) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = (createGroupState as CreateGroupState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (groupName.isNotBlank()) {
                            groupViewModel.createGroup(groupName, userId, groupDescription)
                            groupName = ""
                            groupDescription = ""
                        }
                    },
                    enabled = createGroupState !is CreateGroupState.Loading && groupName.isNotBlank()
                ) {
                    Text("Crear")
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showCreateDialog = false
                        groupName = ""
                        groupDescription = ""
                        groupViewModel.resetCreateGroupState()
                    },
                    enabled = createGroupState !is CreateGroupState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )

    }

    groupForAction?.let { group ->
        GroupActionConfirmDialog(
            groupName = group.nombreGrupo,
            isCreator = groupViewModel.isCurrentUserCreator(group),
            isLoading = groupActionState is GroupActionState.Loading,
            errorMessage = (groupActionState as? GroupActionState.Error)?.message,
            onConfirm = {
                if (groupViewModel.isCurrentUserCreator(group)) groupViewModel.deleteGroup(group.id)
                else groupViewModel.leaveGroup(group.id)
            },
            onDismiss = {
                groupForAction = null
                groupViewModel.resetGroupActionState()
            }
        )
    }

    if (showJoinDialog) {
        val isJoinLoading = joinGroupState is JoinGroupState.Loading
        AlertDialog(
            onDismissRequest = {
                if (!isJoinLoading) {
                    showJoinDialog = false
                    joinLinkInput = ""
                    joinError = ""
                    groupViewModel.resetJoinGroupState()
                }
            },
            title = { Text("Unirse con enlace") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = joinLinkInput,
                        onValueChange = { joinLinkInput = it },
                        label = { Text("Enlace o ID del grupo") },
                        placeholder = { Text("splitapp://join?groupId=... o solo el ID") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isJoinLoading
                    )
                    if (joinError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = joinError,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    if (isJoinLoading) {
                        Spacer(modifier = Modifier.height(8.dp))
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val extractedId = if (joinLinkInput.contains("groupId=")) {
                            joinLinkInput.substringAfter("groupId=").trim()
                        } else {
                            joinLinkInput.trim()
                        }
                        if (extractedId.isNotBlank()) {
                            joinError = ""
                            groupViewModel.joinGroupByLink(
                                groupId = extractedId,
                                onSuccess = {
                                    showJoinDialog = false
                                    joinLinkInput = ""
                                },
                                onError = { message -> joinError = message }
                            )
                        } else {
                            joinError = "Introduce un enlace o ID válido"
                        }
                    },
                    enabled = joinLinkInput.isNotBlank() && !isJoinLoading
                ) {
                    Text("Unirse")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showJoinDialog = false
                        joinLinkInput = ""
                        joinError = ""
                        groupViewModel.resetJoinGroupState()
                    },
                    enabled = !isJoinLoading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Grupos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { showJoinDialog = true }) {
                        Icon(Icons.Default.Link, contentDescription = "Unirse con enlace")
                    }
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Perfil")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear Grupo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total que debo",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = globalBalanceState.totalQueDebo.formatEuros(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total que me deben",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = globalBalanceState.totalQueMeDeben.formatEuros(),
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (groups.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No tienes grupos",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Presiona el botón + para crear uno",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groups, key = { it.id }) { group ->
                        GroupCard(
                            group = group,
                            userBalance = group.balancesCentimos[userId] ?: 0L,
                            onClick = { onNavigateToGroupDetail(group.id) },
                            onLongClick = { groupForAction = group }
                        )
                    }
                }
            }
        }
    }

    if (showPhotoOptionsDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoOptionsDialog = false },
            title = { Text("Cambiar foto de perfil") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextButton(
                        onClick = {
                            showPhotoOptionsDialog = false
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
                            showPhotoOptionsDialog = false
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
                TextButton(onClick = { showPhotoOptionsDialog = false }) { Text("Cancelar") }
            }
        )
    }

    if (showProfileDialog) {
        AlertDialog(
            onDismissRequest = { showProfileDialog = false },
            title = { Text("Perfil") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (profileState) {
                        is com.example.splitapp.ui.profile.ProfileState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                        }
                        is com.example.splitapp.ui.profile.ProfileState.Success -> {
                            val user = (profileState as com.example.splitapp.ui.profile.ProfileState.Success).user

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
                                    onClick = { showPhotoOptionsDialog = true },
                                    enabled = uploadPhotoState !is UploadPhotoState.Loading
                                ) {
                                    Text("Cambiar foto")
                                }
                                if (uploadPhotoState is UploadPhotoState.Loading) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                                }
                                if (uploadPhotoState is UploadPhotoState.Error) {
                                    Text(
                                        text = (uploadPhotoState as UploadPhotoState.Error).message,
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
                                onValueChange = { newValue ->
                                    val sanitized = newValue.replace(" ", "").lowercase()
                                    if (sanitized.length <= 15) profileNameInput = sanitized
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = updateProfileState !is com.example.splitapp.ui.profile.UpdateProfileState.Loading,
                                supportingText = {
                                    Text(
                                        text = "Sin espacios · ${profileNameInput.length}/15 caracteres",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodyMedium)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Rol: ${if (user.role == "admin") "Administrador" else "Usuario"}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            if (updateProfileState is com.example.splitapp.ui.profile.UpdateProfileState.Loading) {
                                CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                            }
                            if (profileError.isNotBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(text = profileError, color = MaterialTheme.colorScheme.error)
                            }
                            if (updateProfileState is com.example.splitapp.ui.profile.UpdateProfileState.Error) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = (updateProfileState as com.example.splitapp.ui.profile.UpdateProfileState.Error).message,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            if (updateProfileState is com.example.splitapp.ui.profile.UpdateProfileState.Success) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Perfil actualizado correctamente",
                                    color = MaterialTheme.colorScheme.secondary,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                        is com.example.splitapp.ui.profile.ProfileState.Error -> {
                            Text(
                                text = (profileState as com.example.splitapp.ui.profile.ProfileState.Error).message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is com.example.splitapp.ui.profile.ProfileState.Idle -> {
                            Text("No se pudo cargar el perfil")
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (profileNameInput.isNotBlank()) {
                            profileError = ""
                            profileViewModel.updateUserName(profileNameInput)
                        } else {
                            profileError = "El nombre no puede estar vacío"
                        }
                    },
                    enabled = updateProfileState !is com.example.splitapp.ui.profile.UpdateProfileState.Loading
                ) {
                    Text("Guardar cambios")
                }
            },
            dismissButton = {
                Column {
                    TextButton(onClick = { showProfileDialog = false }) {
                        Text("Cerrar")
                    }
                    TextButton(
                        onClick = {
                            showProfileDialog = false
                            onNavigateToLogin()
                        },
                        enabled = updateProfileState !is com.example.splitapp.ui.profile.UpdateProfileState.Loading
                    ) {
                        Text("Cerrar Sesión")
                    }
                }
            }
        )
    }
}

private fun createTempImageUri(context: android.content.Context): Uri {
    val tempFile = java.io.File.createTempFile("profile_", ".jpg", context.cacheDir)
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        tempFile
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GroupCard(
    group: Group,
    userBalance: Long,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = group.nombreGrupo, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Miembros activos: ${group.miembrosActivos.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(14.dp))
            val (balanceLabel, balanceColor) = when {
                userBalance > 10L  -> "Me deben: ${userBalance.formatEuros()}" to MaterialTheme.colorScheme.primary
                userBalance < -10L -> "Les debo: ${(-userBalance).formatEuros()}" to MaterialTheme.colorScheme.error
                else               -> "Saldo: ${userBalance.formatEuros()}" to MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = balanceLabel,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = balanceColor
            )
        }
    }
}