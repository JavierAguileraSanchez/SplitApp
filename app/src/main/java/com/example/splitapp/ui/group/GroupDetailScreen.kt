package com.example.splitapp.ui.group

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.splitapp.domain.usecase.group.Transferencia
import com.example.splitapp.ui.expense.AddExpenseState
import com.example.splitapp.ui.expense.ExpensesUiState
import com.example.splitapp.ui.expense.ExpenseViewModel
import com.example.splitapp.ui.expense.SettleDebtState
import com.example.splitapp.ui.group.components.AddExpenseDialog
import com.example.splitapp.ui.group.components.AddMemberDialog
import com.example.splitapp.ui.group.components.BalancesSection
import com.example.splitapp.ui.group.components.ExpensesSection
import com.example.splitapp.ui.group.components.GroupDescriptionAccordion
import com.example.splitapp.ui.group.components.SettleDebtDialog
import com.example.splitapp.ui.group.components.SplitMode
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    val groupActionState by groupViewModel.groupActionState.collectAsState()
    val miembrosNombres by expenseViewModel.miembrosNombres.collectAsState()

    val group = groups.firstOrNull { it.id == groupId }
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isCreator = group?.let { groupViewModel.isCurrentUserCreator(it) } ?: false

    var selectedTab by remember { mutableStateOf(0) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    var showSettleDebtDialog by remember { mutableStateOf(false) }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var showGroupActionDialog by remember { mutableStateOf(false) }
    var showTopBarMenu by remember { mutableStateOf(false) }
    var isDescriptionExpanded by remember { mutableStateOf(false) }
    var optimizedTransactions by remember { mutableStateOf<List<Transferencia>>(emptyList()) }

    // Estado de AddExpenseDialog
    var addTitle by remember { mutableStateOf("") }
    var addAmount by remember { mutableStateOf("") }
    var selectedPagadorId by remember { mutableStateOf(currentUserId) }
    val selectedParticipants = remember { mutableStateMapOf<String, Boolean>() }
    var customSplitEnabled by remember { mutableStateOf(false) }
    var customSplitMode by remember { mutableStateOf(SplitMode.Amounts) }
    val customSplitValues = remember { mutableStateMapOf<String, String>() }

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(showAddExpenseDialog, group?.miembrosActivos) {
        if (showAddExpenseDialog) {
            selectedParticipants.clear(); customSplitValues.clear()
            customSplitEnabled = false; customSplitMode = SplitMode.Amounts
            group?.miembrosActivos?.forEach { selectedParticipants[it] = true }
            addTitle = ""; addAmount = ""; selectedPagadorId = currentUserId
        }
    }
    LaunchedEffect(showSettleDebtDialog, group?.balancesCentimos) {
        if (showSettleDebtDialog && group != null)
            optimizedTransactions = expenseViewModel.calcularLiquidacionOptima(group.balancesCentimos)
    }
    LaunchedEffect(group?.miembros) {
        if (group != null) expenseViewModel.loadMiembrosNombres(group.miembros)
    }
    LaunchedEffect(addExpenseState) {
        if (addExpenseState is AddExpenseState.Success) { showAddExpenseDialog = false; expenseViewModel.resetAddExpenseState() }
    }
    LaunchedEffect(settleDebtState) {
        if (settleDebtState is SettleDebtState.Success) { showSettleDebtDialog = false; expenseViewModel.resetSettleDebtState() }
    }
    LaunchedEffect(addMemberState) {
        if (addMemberState is AddMemberState.Success) {
            showAddMemberDialog = false
            groupViewModel.resetAddMemberState(); groupViewModel.resetSearchState()
        }
    }
    LaunchedEffect(groupActionState) {
        if (groupActionState is GroupActionState.Success) { groupViewModel.resetGroupActionState(); onNavigateBack() }
    }
    LaunchedEffect(Unit) {
        groupViewModel.exportEvent.collect { event ->
            when (event) {
                is ExportEvent.Success -> snackbarHostState.showSnackbar("Historial exportado con éxito en ${event.filePath}")
                is ExportEvent.Error   -> snackbarHostState.showSnackbar(event.message)
                is ExportEvent.Loading -> {}
            }
        }
    }

    val activeParticipants = selectedParticipants.filter { it.value }.keys.toList()
    val totalAmount = addAmount.toDoubleOrNull() ?: 0.0
    val customSplitSum = activeParticipants.sumOf { customSplitValues[it]?.toDoubleOrNull() ?: 0.0 }
    val customSplitValid = customSplitEnabled && activeParticipants.isNotEmpty() && totalAmount > 0.0 &&
        activeParticipants.all { customSplitValues[it]?.toDoubleOrNull() != null } &&
        if (customSplitMode == SplitMode.Amounts) abs(customSplitSum - totalAmount) <= 0.01
        else abs(customSplitSum - 100.0) <= 0.5

    val customSplitValidationMessage = when {
        !customSplitEnabled -> null
        totalAmount <= 0.0  -> "Ingresa un monto total válido"
        activeParticipants.isEmpty() -> "Selecciona al menos un participante"
        activeParticipants.any { customSplitValues[it].isNullOrBlank() } -> "Completa todos los valores de división personalizada"
        customSplitMode == SplitMode.Amounts && abs(customSplitSum - totalAmount) > 0.01 -> "La suma de los montos debe coincidir con el total"
        customSplitMode == SplitMode.Percentages && abs(customSplitSum - 100.0) > 0.5 -> "La suma de los porcentajes debe ser 100%"
        else -> null
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
                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Enlace de invitación", link))
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
                        DropdownMenu(expanded = showTopBarMenu, onDismissRequest = { showTopBarMenu = false }) {
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
                                onClick = { showGroupActionDialog = true; showTopBarMenu = false }
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
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp)
        ) {
            if (group == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (groups.isEmpty()) "Cargando grupo..." else "Grupo no encontrado",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                return@Box
            }

            Column(modifier = Modifier.fillMaxSize()) {
                GroupDescriptionAccordion(
                    descripcion = group.descripcion,
                    isExpanded = isDescriptionExpanded,
                    onToggle = { isDescriptionExpanded = !isDescriptionExpanded }
                )
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }, text = { Text("Balances") })
                    Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }, text = { Text("Gastos") })
                }
                Spacer(modifier = Modifier.height(16.dp))
                if (selectedTab == 0) {
                    BalancesSection(group = group, userNames = miembrosNombres, onLiquidarDebt = { showSettleDebtDialog = true })
                } else {
                    when (val state = expensesState) {
                        is ExpensesUiState.Loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                        is ExpensesUiState.Error   -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(text = state.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        }
                        is ExpensesUiState.Success -> ExpensesSection(
                            expenses = state.expenses,
                            userNames = miembrosNombres,
                            onDeleteExpense = { expenseViewModel.deleteExpense(it) }
                        )
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
            onParticipantToggle = { selectedParticipants[it] = !(selectedParticipants[it] ?: false) },
            pagadorId = selectedPagadorId,
            onPagadorChange = { selectedPagadorId = it },
            isCustomSplitEnabled = customSplitEnabled,
            onCustomSplitEnabledChange = { enabled -> customSplitEnabled = enabled; if (!enabled) customSplitValues.clear() },
            customSplitMode = customSplitMode,
            onCustomSplitModeChange = { customSplitMode = it },
            customSplitValues = customSplitValues,
            onCustomSplitValueChange = { memberId, value -> customSplitValues[memberId] = value },
            splitValidationMessage = customSplitValidationMessage,
            titleValue = addTitle,
            onTitleChange = { addTitle = it },
            amountValue = addAmount,
            onAmountChange = { addAmount = it },
            isLoading = addExpenseState is AddExpenseState.Loading,
            errorMessage = (addExpenseState as? AddExpenseState.Error)?.message,
            isConfirmEnabled = (!customSplitEnabled && selectedParticipants.any { it.value }) || (customSplitEnabled && customSplitValid),
            onDismiss = { showAddExpenseDialog = false; expenseViewModel.resetAddExpenseState() },
            onConfirm = {
                val amount = addAmount.toDoubleOrNull() ?: 0.0
                val participantesActivos = selectedParticipants.filter { it.value }.keys.toList()
                if (addTitle.isNotBlank() && amount > 0.0 && participantesActivos.isNotEmpty() && selectedPagadorId.isNotBlank()) {
                    if (customSplitEnabled) {
                        val distribution = participantesActivos.associateWith { memberId ->
                            val raw = customSplitValues[memberId]?.toDoubleOrNull() ?: 0.0
                            if (customSplitMode == SplitMode.Percentages) (raw / 100.0) * amount else raw
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
            isLoading = settleDebtState is SettleDebtState.Loading,
            errorMessage = (settleDebtState as? SettleDebtState.Error)?.message,
            onDismiss = { showSettleDebtDialog = false; expenseViewModel.resetSettleDebtState() },
            onConfirm = { expenseViewModel.settleDebt() }
        )
    }

    if (showAddMemberDialog && group != null) {
        AddMemberDialog(
            currentUserId = currentUserId,
            userSearchState = userSearchState,
            addMemberState = addMemberState,
            onSearchUsers = groupViewModel::searchUsers,
            onAddMember = { userId -> groupViewModel.addMemberById(groupId, userId) },
            onResetSearch = groupViewModel::resetSearchState,
            onResetAddMember = groupViewModel::resetAddMemberState,
            onDismiss = { showAddMemberDialog = false }
        )
    }

    if (showGroupActionDialog && group != null) {
        GroupActionConfirmDialog(
            groupName = group.nombreGrupo,
            isCreator = isCreator,
            isLoading = groupActionState is GroupActionState.Loading,
            errorMessage = (groupActionState as? GroupActionState.Error)?.message,
            onConfirm = { if (isCreator) groupViewModel.deleteGroup(group.id) else groupViewModel.leaveGroup(group.id) },
            onDismiss = { showGroupActionDialog = false; groupViewModel.resetGroupActionState() }
        )
    }
}
