package com.example.splitapp.ui.group

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitapp.data.model.Expense
import kotlin.math.abs
import com.example.splitapp.data.model.Group
import com.example.splitapp.domain.usecase.group.Transferencia
import com.example.splitapp.ui.components.UserAvatar
import com.example.splitapp.ui.expense.ExpenseViewModel
import com.example.splitapp.ui.expense.ExpensesUiState
import com.example.splitapp.util.formatEuros
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType

enum class SplitMode {
    Amounts,
    Percentages
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: String,
    expenseViewModel: ExpenseViewModel,
    groupViewModel: GroupViewModel,
    onNavigateBack: () -> Unit
) {
    val groups by groupViewModel.groups.collectAsState()
    val expensesState by expenseViewModel.expensesState.collectAsState()
    val addExpenseState by expenseViewModel.addExpenseState.collectAsState()
    val settleDebtState by expenseViewModel.settleDebtState.collectAsState()
    val addMemberState by groupViewModel.addMemberState.collectAsState()
    val userSearchState by groupViewModel.userSearchState.collectAsState()
    val searchQuery by groupViewModel.searchQuery.collectAsState()

    val group = groups.firstOrNull { it.id == groupId }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val miembrosNombres by expenseViewModel.miembrosNombres.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSettleDebtDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showGroupActionDialog by remember { mutableStateOf(false) }
    var showTopBarMenu by remember { mutableStateOf(false) }
    val groupActionState by groupViewModel.groupActionState.collectAsState()
    val isCreator = group?.let { groupViewModel.isCurrentUserCreator(it) } ?: false
    var addTitle by remember { mutableStateOf("") }
    var addAmount by remember { mutableStateOf("") }
    val selectedParticipants = remember { mutableStateMapOf<String, Boolean>() }
    var customSplitEnabled by remember { mutableStateOf(false) }
    var customSplitMode by remember { mutableStateOf(SplitMode.Amounts) }
    val customSplitValues = remember { mutableStateMapOf<String, String>() }
    var customSplitError by remember { mutableStateOf<String?>(null) }
    var memberInput by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf("") }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var optimizedTransactions by remember { mutableStateOf<List<Transferencia>>(emptyList()) }
    var selectedPagadorId by remember { mutableStateOf(currentUserId) }

    LaunchedEffect(showAddExpenseDialog, group?.miembrosActivos) {
        if (showAddExpenseDialog) {
            selectedParticipants.clear()
            customSplitValues.clear()
            customSplitEnabled = false
            customSplitMode = SplitMode.Amounts
            group?.miembrosActivos?.forEach { selectedParticipants[it] = true }
            addTitle = ""
            addAmount = ""
            selectedPagadorId = currentUserId
        }
    }

    LaunchedEffect(showSettleDebtDialog, group?.balancesCentimos) {
        if (showSettleDebtDialog && group != null) {
            optimizedTransactions = expenseViewModel.calcularLiquidacionOptima(group.balancesCentimos)
        }
    }

    val activeParticipants = selectedParticipants.filter { it.value }.keys.toList()
    val totalAmount = addAmount.toDoubleOrNull() ?: 0.0
    val customSplitSum = activeParticipants.sumOf { id ->
        customSplitValues[id]?.toDoubleOrNull() ?: 0.0
    }
    val customSplitValid = customSplitEnabled && activeParticipants.isNotEmpty() && totalAmount > 0.0 &&
        activeParticipants.all { id -> customSplitValues[id]?.toDoubleOrNull() != null } &&
        if (customSplitMode == SplitMode.Amounts) abs(customSplitSum - totalAmount) <= 0.01
        else abs(customSplitSum - 100.0) <= 0.5

    val customSplitValidationMessage = if (!customSplitEnabled) {
        null
    } else if (totalAmount <= 0.0) {
        "Ingresa un monto total válido"
    } else if (activeParticipants.isEmpty()) {
        "Selecciona al menos un participante"
    } else if (activeParticipants.any { id -> customSplitValues[id].isNullOrBlank() }) {
        "Completa todos los valores de división personalizada"
    } else if (customSplitMode == SplitMode.Amounts && abs(customSplitSum - totalAmount) > 0.01) {
        "La suma de los montos debe coincidir con el total"
    } else if (customSplitMode == SplitMode.Percentages && abs(customSplitSum - 100.0) > 0.5) {
        "La suma de los porcentajes debe ser 100%"
    } else {
        null
    }

    LaunchedEffect(group?.miembros) {
        if (group != null) {
            expenseViewModel.loadMiembrosNombres(group.miembros)
        }
    }

    LaunchedEffect(addExpenseState) {
        if (addExpenseState is com.example.splitapp.ui.expense.AddExpenseState.Success) {
            showAddExpenseDialog = false
            expenseViewModel.resetAddExpenseState()
        }
    }

    LaunchedEffect(settleDebtState) {
        if (settleDebtState is com.example.splitapp.ui.expense.SettleDebtState.Success) {
            showSettleDebtDialog = false
            expenseViewModel.resetSettleDebtState()
        }
    }

    LaunchedEffect(addMemberState) {
        if (addMemberState is AddMemberState.Success) {
            showAddMemberDialog = false
            memberInput = ""
            selectedUserId = ""
            groupViewModel.resetAddMemberState()
            groupViewModel.resetSearchState()
        }
    }

    LaunchedEffect(groupActionState) {
        if (groupActionState is GroupActionState.Success) {
            groupViewModel.resetGroupActionState()
            onNavigateBack()
        }
    }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        groupViewModel.exportEvent.collect { event ->
            when (event) {
                is ExportEvent.Success -> {
                    snackbarHostState.showSnackbar("Historial exportado con éxito en ${event.filePath}")
                }
                is ExportEvent.Error -> {
                    snackbarHostState.showSnackbar("${event.message}")
                }
                is ExportEvent.Loading -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(group?.nombreGrupo ?: "Detalle del Grupo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val link = "splitapp://join?groupId=$groupId"
                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Enlace de invitación", link)
                        clipboard.setPrimaryClip(clip)
                        scope.launch { snackbarHostState.showSnackbar("Enlace de invitación copiado") }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Compartir enlace de invitación")
                    }
                    IconButton(onClick = { showAddMemberDialog = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Agregar miembro")
                    }
                    Box {
                        IconButton(onClick = { showTopBarMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(
                            expanded = showTopBarMenu,
                            onDismissRequest = { showTopBarMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Exportar gastos") },
                                leadingIcon = { Icon(Icons.Default.FileDownload, contentDescription = null) },
                                onClick = {
                                    val externalDir = context.getExternalFilesDir(null)
                                    val targetDir = if (externalDir != null && externalDir.canWrite()) externalDir else context.cacheDir
                                    groupViewModel.exportGroupExpenses(groupId, targetDir)
                                    showTopBarMenu = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text(if (isCreator) "Eliminar grupo" else "Salirse del grupo") },
                                leadingIcon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null) },
                                onClick = {
                                    showGroupActionDialog = true
                                    showTopBarMenu = false
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddExpenseDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir gasto")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (group == null) {
                val loadingText = if (groups.isEmpty()) "Cargando grupo..." else "Grupo no encontrado"
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = loadingText, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                }
                return@Box
            }

            Column(modifier = Modifier.fillMaxSize()) {
                GroupDescriptionAccordion(
                    descripcion = group.descripcion,
                    isExpanded = isDescriptionExpanded,
                    onToggle = { isDescriptionExpanded = !isDescriptionExpanded }
                )

                PrimaryTabRow(
                    selectedTabIndex = selectedTab
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Balances") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Gastos") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedTab == 0) {
                    BalancesSection(group = group, userNames = miembrosNombres, onLiquidarDebt = { showSettleDebtDialog = true })
                } else {
                    when (val state = expensesState) {
                        is ExpensesUiState.Loading -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        is ExpensesUiState.Error -> {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(
                                    text = state.message,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        is ExpensesUiState.Success -> {
                            ExpensesSection(
                                expenses = state.expenses,
                                userNames = miembrosNombres,
                                onDeleteExpense = { expenseViewModel.deleteExpense(it) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddExpenseDialog && group != null) {
        AddExpenseDialog(
            group = group,
            selectedParticipants = selectedParticipants,
            userNames = miembrosNombres,
            onParticipantToggle = { member ->
                selectedParticipants[member] = !(selectedParticipants[member] ?: false)
            },
            pagadorId = selectedPagadorId,
            onPagadorChange = { selectedPagadorId = it },
            isCustomSplitEnabled = customSplitEnabled,
            onCustomSplitEnabledChange = { enabled ->
                customSplitEnabled = enabled
                if (!enabled) {
                    customSplitValues.clear()
                    customSplitError = null
                }
            },
            customSplitMode = customSplitMode,
            onCustomSplitModeChange = { customSplitMode = it },
            customSplitValues = customSplitValues,
            onCustomSplitValueChange = { memberId, value ->
                customSplitValues[memberId] = value
            },
            splitValidationMessage = customSplitValidationMessage,
            titleValue = addTitle,
            onTitleChange = { addTitle = it },
            amountValue = addAmount,
            onAmountChange = { addAmount = it },
            isLoading = addExpenseState is com.example.splitapp.ui.expense.AddExpenseState.Loading,
            errorMessage = (addExpenseState as? com.example.splitapp.ui.expense.AddExpenseState.Error)?.message,
            isConfirmEnabled = (!customSplitEnabled && selectedParticipants.any { it.value }) || (customSplitEnabled && customSplitValid),
            onDismiss = {
                showAddExpenseDialog = false
                expenseViewModel.resetAddExpenseState()
            },
            onConfirm = {
                val amount = addAmount.toDoubleOrNull() ?: 0.0
                val participantesActivos = selectedParticipants.filter { it.value }.keys.toList()
                if (addTitle.isNotBlank() && amount > 0.0 && participantesActivos.isNotEmpty() && selectedPagadorId.isNotBlank()) {
                    if (customSplitEnabled) {
                        val distribution = participantesActivos.associateWith { memberId ->
                            val rawValue = customSplitValues[memberId]?.toDoubleOrNull() ?: 0.0
                            if (customSplitMode == SplitMode.Percentages) {
                                (rawValue / 100.0) * amount
                            } else {
                                rawValue
                            }
                        }
                        expenseViewModel.addExpense(addTitle, amount, selectedPagadorId, participantesActivos, distribution)
                    } else {
                        expenseViewModel.addExpense(addTitle, amount, selectedPagadorId, participantesActivos)
                    }
                }
            }
        )
    }

    if (showSettleDebtDialog && group != null) {
        SettleDebtDialog(
            transactions = optimizedTransactions,
            userNames = miembrosNombres,
            isLoading = settleDebtState is com.example.splitapp.ui.expense.SettleDebtState.Loading,
            errorMessage = (settleDebtState as? com.example.splitapp.ui.expense.SettleDebtState.Error)?.message,
            onDismiss = {
                showSettleDebtDialog = false
                expenseViewModel.resetSettleDebtState()
            },
            onConfirm = {
                expenseViewModel.settleDebt()
            }
        )
    }

    if (showAddMemberDialog && group != null) {
        AlertDialog(
            onDismissRequest = {
                showAddMemberDialog = false
                memberInput = ""
                selectedUserId = ""
                groupViewModel.resetAddMemberState()
                groupViewModel.resetSearchState()
            },
            title = { Text("Añadir miembro al grupo") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = memberInput,
                        onValueChange = { newValue ->
                            memberInput = newValue
                            selectedUserId = ""
                            groupViewModel.searchUsers(newValue)
                        },
                        label = { Text("Buscar por nombre de usuario") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = addMemberState !is AddMemberState.Loading
                    )

                    if (memberInput.isNotBlank() && userSearchState !is UserSearchState.Idle) {
                        Spacer(modifier = Modifier.height(12.dp))

                        when (userSearchState) {
                            is UserSearchState.Loading -> {
                                CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                            }
                            is UserSearchState.Success -> {
                                val results = (userSearchState as UserSearchState.Success).results
                                    .filter { it.id != currentUserId }
                                if (results.isNotEmpty()) {
                                    LazyColumn(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 200.dp)
                                    ) {
                                        items(results) { user ->
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable {
                                                        memberInput = user.nombre
                                                        selectedUserId = user.id
                                                        groupViewModel.resetSearchState()
                                                    }
                                                    .padding(4.dp)
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    UserAvatar(name = user.nombre, size = 32.dp)
                                                    Spacer(modifier = Modifier.width(10.dp))
                                                    Text(
                                                        text = user.nombre,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "No se encontraron usuarios",
                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            is UserSearchState.Error -> {
                                Text(
                                    text = (userSearchState as UserSearchState.Error).message,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.error
                                )
                            }
                            is UserSearchState.Idle -> {}
                        }
                    }

                    if (addMemberState is AddMemberState.Error) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = (addMemberState as AddMemberState.Error).message,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.error
                        )
                    }
                    if (addMemberState is AddMemberState.Loading) {
                        Spacer(modifier = Modifier.height(12.dp))
                        CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (selectedUserId.isNotBlank()) {
                            groupViewModel.addMemberById(groupId = groupId, userId = selectedUserId)
                        }
                    },
                    enabled = selectedUserId.isNotBlank() && addMemberState !is AddMemberState.Loading
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddMemberDialog = false
                        memberInput = ""
                        selectedUserId = ""
                        groupViewModel.resetAddMemberState()
                        groupViewModel.resetSearchState()
                    },
                    enabled = addMemberState !is AddMemberState.Loading
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
    if (showGroupActionDialog && group != null) {
        GroupActionConfirmDialog(
            groupName = group.nombreGrupo,
            isCreator = isCreator,
            isLoading = groupActionState is GroupActionState.Loading,
            errorMessage = (groupActionState as? GroupActionState.Error)?.message,
            onConfirm = {
                if (isCreator) groupViewModel.deleteGroup(group.id)
                else groupViewModel.leaveGroup(group.id)
            },
            onDismiss = {
                showGroupActionDialog = false
                groupViewModel.resetGroupActionState()
            }
        )
    }
}

@Composable
private fun GroupDescriptionAccordion(
    descripcion: String,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    if (descripcion.isBlank()) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Descripción del grupo",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                              else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Contraer" else "Expandir",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        AnimatedVisibility(visible = isExpanded) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = descripcion,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
private fun BalancesSection(
    group: Group,
    userNames: Map<String, String>,
    onLiquidarDebt: () -> Unit
) {
    if (group.miembros.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "No hay miembros registrados", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Balances actuales",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Mostrar todos (activos + inactivos con saldo pendiente)
                items(group.miembros) { memberId ->
                    val balance = group.balancesCentimos[memberId] ?: 0L
                    val isInactive = group.estadoMiembros[memberId] == false
                    BalanceItem(
                        memberId   = memberId,
                        balance    = balance,
                        isInactive = isInactive,
                        userNames  = userNames
                    )
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onLiquidarDebt,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
            text = { Text("Liquidar deuda") }
        )
    }
}

@Composable
private fun BalanceItem(
    memberId: String,
    balance: Long,
    isInactive: Boolean,
    userNames: Map<String, String>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayName = userNames[memberId] ?: memberId
            UserAvatar(name = displayName, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                    if (isInactive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "inactivo",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        balance > 0L -> "Le deben: ${balance.formatEuros()}"
                        balance < 0L -> "Debe: ${(-balance).formatEuros()}"
                        else -> "Saldo neutro"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = when {
                        balance > 0L -> MaterialTheme.colorScheme.primary
                        balance < 0L -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}

@Composable
private fun ExpensesSection(
    expenses: List<Expense>,
    userNames: Map<String, String>,
    onDeleteExpense: (String) -> Unit
) {
    if (expenses.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Aún no hay gastos en este grupo",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Registra tu primer gasto para ver el resumen financiero",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }

    val expensesByCategory = expenses
        .groupBy { it.concepto }
        .mapValues { entry -> entry.value.sumOf { it.montoCentimos } }

    Column(modifier = Modifier.fillMaxSize()) {
        GroupExpensesPieChart(expensesByCategory = expensesByCategory)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(expenses) { expense ->
                ExpenseItem(
                    expense = expense,
                    userNames = userNames,
                    onDeleteExpense = onDeleteExpense
                )
            }
        }
    }
}

@Composable
private fun ExpenseItem(
    expense: Expense,
    userNames: Map<String, String>,
    onDeleteExpense: (String) -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar gasto") },
            text = { Text("¿Eliminar \"${expense.concepto}\"? Los balances del grupo se actualizarán.") },
            confirmButton = {
                Button(
                    onClick = { onDeleteExpense(expense.id); showDeleteConfirm = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancelar") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = expense.concepto,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar gasto",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Total: ${expense.montoCentimos.formatEuros()}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(6.dp))
            val payerName = userNames[expense.pagadoPor] ?: expense.pagadoPor
            Text(text = "Pagado por: $payerName", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Fecha: ${expense.createdAt?.toDate()?.formatToDisplay() ?: "—"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun java.util.Date.formatToDisplay(): String {
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return formatter.format(this)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    group: Group,
    selectedParticipants: Map<String, Boolean>,
    userNames: Map<String, String>,
    onParticipantToggle: (String) -> Unit,
    pagadorId: String,
    onPagadorChange: (String) -> Unit,
    isCustomSplitEnabled: Boolean,
    onCustomSplitEnabledChange: (Boolean) -> Unit,
    customSplitMode: SplitMode,
    onCustomSplitModeChange: (SplitMode) -> Unit,
    customSplitValues: Map<String, String>,
    onCustomSplitValueChange: (String, String) -> Unit,
    splitValidationMessage: String?,
    titleValue: String,
    onTitleChange: (String) -> Unit,
    amountValue: String,
    onAmountChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    isConfirmEnabled: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val participantesActivos = selectedParticipants.filter { it.value }.keys
    val splitHint = if (customSplitMode == SplitMode.Percentages) "Porcentaje" else "Monto"
    var pagadorExpanded by remember { mutableStateOf(false) }
    // Preview de cuotas en Double — solo para mostrar al usuario, no se persiste
    val perParticipantAmounts = participantesActivos.associateWith { memberId ->
        val rawValue = customSplitValues[memberId]?.toDoubleOrNull()
        when {
            isCustomSplitEnabled && customSplitMode == SplitMode.Percentages -> (rawValue ?: 0.0) / 100.0 * (amountValue.toDoubleOrNull() ?: 0.0)
            isCustomSplitEnabled -> rawValue ?: 0.0
            participantesActivos.isNotEmpty() -> (amountValue.toDoubleOrNull() ?: 0.0) / participantesActivos.size
            else -> 0.0
        }
    }
    val assignedTotal = perParticipantAmounts.values.sum()
    val roundingDifference = (amountValue.toDoubleOrNull() ?: 0.0) - assignedTotal
    val roundingMessage = when {
        abs(roundingDifference) < 0.005 -> null
        roundingDifference > 0 -> "Faltan ${String.format("%.2f", roundingDifference)}€ para completar el total"
        else -> "Te pasas por ${String.format("%.2f", -roundingDifference)}€"
    }
    // Lista de miembros elegibles como pagadores — solo activos
    val miembrosElegibles = group.miembrosActivos

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir nuevo gasto") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = titleValue,
                    onValueChange = onTitleChange,
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = amountValue,
                    onValueChange = onAmountChange,
                    label = { Text("Monto") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = pagadorExpanded,
                    onExpandedChange = { if (!isLoading) pagadorExpanded = it }
                ) {
                    OutlinedTextField(
                        value = userNames[pagadorId] ?: pagadorId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Pagado por") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pagadorExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = pagadorExpanded,
                        onDismissRequest = { pagadorExpanded = false }
                    ) {
                        miembrosElegibles.forEach { memberId ->
                            DropdownMenuItem(
                                text = { Text(userNames[memberId] ?: memberId) },
                                onClick = {
                                    onPagadorChange(memberId)
                                    pagadorExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "División personalizada", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isCustomSplitEnabled,
                        onCheckedChange = onCustomSplitEnabledChange,
                        enabled = !isLoading
                    )
                }

                if (isCustomSplitEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Modo de división", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = customSplitMode == SplitMode.Amounts,
                            onClick = { onCustomSplitModeChange(SplitMode.Amounts) },
                            enabled = !isLoading
                        )
                        Text(text = "Monto", modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = customSplitMode == SplitMode.Percentages,
                            onClick = { onCustomSplitModeChange(SplitMode.Percentages) },
                            enabled = !isLoading
                        )
                        Text(text = "%", modifier = Modifier.padding(start = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Introduce $splitHint por participante", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        participantesActivos.toList().forEach { memberId ->
                            val displayName = userNames[memberId] ?: memberId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = displayName, modifier = Modifier.weight(1f))
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = customSplitValues[memberId] ?: "",
                                    onValueChange = { onCustomSplitValueChange(memberId, it) },
                                    label = { Text(splitHint) },
                                    modifier = Modifier.width(120.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    enabled = !isLoading
                                )
                            }
                        }
                    }
                    if (!splitValidationMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = splitValidationMessage, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "¿Quiénes participan?", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    miembrosElegibles.forEach { memberId ->
                        val checked = selectedParticipants[memberId] == true
                        val displayName = userNames[memberId] ?: memberId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onParticipantToggle(memberId) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onParticipantToggle(memberId) },
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = displayName)
                        }
                    }
                }

                if (participantesActivos.isNotEmpty() && (amountValue.toDoubleOrNull() ?: 0.0) > 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Resumen de Impacto", style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    perParticipantAmounts.forEach { (memberId, amount) ->
                        val displayName = userNames[memberId] ?: memberId
                        Text(
                            text = "• $displayName: ${String.format("%.2f", amount)}€",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!roundingMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = roundingMessage,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.secondary,
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isLoading && isConfirmEnabled
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettleDebtDialog(
    transactions: List<Transferencia>,
    userNames: Map<String, String>,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Liquidar deuda") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Para ponerse al día:",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (transactions.isEmpty()) {
                    Text(text = "No hay deudas pendientes.", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                } else {
                    transactions.forEach { transaction ->
                        val deudorName = userNames[transaction.deudor] ?: transaction.deudor
                        val acreedorName = userNames[transaction.acreedor] ?: transaction.acreedor
                        Text(
                            text = "• $deudorName debe pagar ${transaction.montoCentimos.formatEuros()} a $acreedorName",
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
                }
                if (isLoading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isLoading && transactions.isNotEmpty()) {
                Text("Confirmar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text("Cancelar")
            }
        }
    )
}
