package com.example.splitapp.ui.group

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitapp.data.model.Group
import com.example.splitapp.ui.group.components.CreateGroupDialog
import com.example.splitapp.ui.group.components.GroupCard
import com.example.splitapp.ui.group.components.JoinGroupDialog
import com.example.splitapp.ui.group.components.ProfileDialog
import com.example.splitapp.ui.invitation.InvitationViewModel
import com.example.splitapp.ui.profile.ProfileViewModel
import com.example.splitapp.util.formatEuros
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    groupViewModel: GroupViewModel,
    invitationViewModel: InvitationViewModel,
    onNavigateToGroupDetail: (String) -> Unit,
    onNavigateToInvitations: () -> Unit,
    onNavigateToDebtors: () -> Unit,
    onNavigateToLogin: () -> Unit,
    deepLinkGroupId: String? = null,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var groupName by remember { mutableStateOf("") }
    var groupDescription by remember { mutableStateOf("") }
    var groupMoneda by remember { mutableStateOf("EUR") }
    var profileNameInput by remember { mutableStateOf("") }
    var profileError by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var joinLinkInput by remember { mutableStateOf("") }
    var joinError by remember { mutableStateOf("") }
    var groupForAction by remember { mutableStateOf<Group?>(null) }

    val profileViewModel: ProfileViewModel = viewModel()
    val profileState by profileViewModel.profileState.collectAsState()
    val updateProfileState by profileViewModel.updateProfileState.collectAsState()
    val uploadPhotoState by profileViewModel.uploadPhotoState.collectAsState()
    val updatePhoneState by profileViewModel.updatePhoneState.collectAsState()
    val phoneFromVm by profileViewModel.phoneInput.collectAsState()

    val groups by groupViewModel.groups.collectAsState()
    val globalBalanceState by groupViewModel.globalBalanceState.collectAsState()
    val pendingInvitationsCount by invitationViewModel.pendingCount.collectAsState()
    val createGroupState by groupViewModel.createGroupState.collectAsState()
    val groupActionState by groupViewModel.groupActionState.collectAsState()
    val joinGroupState by groupViewModel.joinGroupState.collectAsState()

    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    LaunchedEffect(deepLinkGroupId) {
        if (!deepLinkGroupId.isNullOrBlank()) { joinLinkInput = deepLinkGroupId; showJoinDialog = true }
    }
    LaunchedEffect(profileState) {
        if (profileState is com.example.splitapp.ui.profile.ProfileState.Success) {
            val u = (profileState as com.example.splitapp.ui.profile.ProfileState.Success).user
            profileNameInput = u.nombre
        }
    }
    LaunchedEffect(phoneFromVm) { phoneInput = phoneFromVm }
    LaunchedEffect(groupActionState) {
        if (groupActionState is GroupActionState.Success) { groupForAction = null; groupViewModel.resetGroupActionState() }
    }
    LaunchedEffect(joinGroupState) {
        if (joinGroupState is JoinGroupState.Success) {
            showJoinDialog = false; joinLinkInput = ""; joinError = ""; groupViewModel.resetJoinGroupState()
        }
    }
    LaunchedEffect(createGroupState) {
        if (createGroupState is CreateGroupState.Success) { showCreateDialog = false; groupViewModel.resetCreateGroupState() }
    }

    if (showCreateDialog) {
        CreateGroupDialog(
            groupName = groupName,
            onGroupNameChange = { groupName = it },
            groupDescription = groupDescription,
            onGroupDescriptionChange = { groupDescription = it },
            selectedMoneda = groupMoneda,
            onMonedaChange = { groupMoneda = it },
            createGroupState = createGroupState,
            onConfirm = {
                if (groupName.isNotBlank()) {
                    groupViewModel.createGroup(groupName, userId, groupDescription, groupMoneda)
                    groupName = ""; groupDescription = ""; groupMoneda = "EUR"
                }
            },
            onDismiss = { showCreateDialog = false; groupName = ""; groupDescription = ""; groupMoneda = "EUR"; groupViewModel.resetCreateGroupState() }
        )
    }

    if (showJoinDialog) {
        JoinGroupDialog(
            joinLinkInput = joinLinkInput,
            onJoinLinkChange = { joinLinkInput = it },
            joinError = joinError,
            joinGroupState = joinGroupState,
            onConfirm = { groupId ->
                joinError = ""
                groupViewModel.joinGroupByLink(
                    groupId = groupId,
                    onSuccess = { showJoinDialog = false; joinLinkInput = "" },
                    onError = { message -> joinError = message }
                )
            },
            onDismiss = { showJoinDialog = false; joinLinkInput = ""; joinError = ""; groupViewModel.resetJoinGroupState() }
        )
    }

    if (showProfileDialog) {
        ProfileDialog(
            profileState = profileState,
            updateProfileState = updateProfileState,
            uploadPhotoState = uploadPhotoState,
            updatePhoneState = updatePhoneState,
            profileNameInput = profileNameInput,
            onProfileNameChange = { newValue ->
                val sanitized = newValue.replace(" ", "")
                if (sanitized.length <= 15) profileNameInput = sanitized
            },
            phoneInput = phoneInput,
            onPhoneChange = { phoneInput = it; profileViewModel.setPhoneInput(it) },
            onSave = {
                if (profileNameInput.isNotBlank()) {
                    profileError = ""
                    profileViewModel.updateUserName(profileNameInput)
                    profileViewModel.savePhone()
                } else profileError = "El nombre no puede estar vacío"
            },
            onUploadPhoto = { uri ->
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@ProfileDialog
                profileViewModel.uploadAndUpdatePhoto(uid, uri)
            },
            onResetUploadPhoto = profileViewModel::resetUploadPhotoState,
            onDismiss = { showProfileDialog = false; profileViewModel.resetUpdatePhoneState() },
            onLogout = { showProfileDialog = false; onNavigateToLogin() },
            isDarkTheme = isDarkTheme,
            onThemeChange = onThemeChange,
            onLanguageChange = onLanguageChange
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
            onDismiss = { groupForAction = null; groupViewModel.resetGroupActionState() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.groups_title)) },
                navigationIcon = {
                    Image(
                        painter = painterResource(R.drawable.logo_splitapp),
                        contentDescription = "SplitApp",
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(32.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { onNavigateToInvitations() }) {
                        BadgedBox(
                            badge = {
                                if (pendingInvitationsCount > 0) {
                                    Badge { Text("$pendingInvitationsCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = stringResource(R.string.invitations_icon_cd))
                        }
                    }
                    IconButton(onClick = { onNavigateToDebtors() }) {
                        Icon(Icons.Default.People, contentDescription = stringResource(R.string.debtors_icon_cd))
                    }
                    IconButton(onClick = { showJoinDialog = true }) {
                        Icon(Icons.Default.Link, contentDescription = stringResource(R.string.groups_join_link_cd))
                    }
                    IconButton(onClick = { showProfileDialog = true }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = stringResource(R.string.groups_profile_cd))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }, modifier = Modifier.padding(16.dp)) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.groups_create_cd))
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.groups_total_owed),
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
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            stringResource(R.string.groups_total_owed_to_me),
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
                            Icons.Default.Group,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(68.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.groups_empty_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            stringResource(R.string.groups_empty_hint),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
}
