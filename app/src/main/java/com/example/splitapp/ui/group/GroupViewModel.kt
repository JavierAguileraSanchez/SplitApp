package com.example.splitapp.ui.group

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.splitapp.data.model.Group
import com.example.splitapp.data.model.User
import com.example.splitapp.domain.repository.GroupRepository
import com.example.splitapp.domain.usecase.expense.ExportExpensesToCsvUseCase
import com.example.splitapp.domain.usecase.group.AddMemberUseCase
import com.example.splitapp.domain.usecase.group.CreateGroupUseCase
import com.example.splitapp.domain.usecase.group.GetGroupsUseCase
import com.example.splitapp.domain.usecase.group.SimplifyDebtsUseCase
import com.example.splitapp.domain.usecase.group.Transferencia
import com.example.splitapp.domain.usecase.invitation.SendInvitationUseCase
import com.example.splitapp.util.AnalyticsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GroupViewModel(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val addMemberUseCase: AddMemberUseCase,
    private val exportExpensesToCsvUseCase: ExportExpensesToCsvUseCase,
    private val sendInvitationUseCase: SendInvitationUseCase,
    userId: String,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val simplifyDebtsUseCase = SimplifyDebtsUseCase()
    val currentUserId: String = userId

    private val _currentUserName = MutableStateFlow("")

    init {
        viewModelScope.launch {
            val names = groupRepository.getUserNames(listOf(userId))
            _currentUserName.value = names[userId] ?: ""
        }
    }

    val groups: StateFlow<List<Group>> = getGroupsUseCase(userId)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    val debtors: StateFlow<List<DebtorSummary>> = groups
        .map { groupList ->
            val debtorMap = mutableMapOf<String, MutableList<DebtorGroupEntry>>()
            for (group in groupList) {
                simplifyDebtsUseCase(group.balancesCentimos)
                    .filter { it.acreedor == userId }
                    .forEach { transfer ->
                        debtorMap.getOrPut(transfer.deudor) { mutableListOf() }
                            .add(DebtorGroupEntry(group.id, group.nombreGrupo, transfer.montoCentimos, group.moneda))
                    }
            }
            val names = if (debtorMap.isNotEmpty())
                groupRepository.getUserNames(debtorMap.keys.toList())
            else
                emptyMap()
            debtorMap.map { (debtorId, entries) ->
                DebtorSummary(
                    debtorId = debtorId,
                    nombre = names[debtorId] ?: debtorId,
                    groupEntries = entries
                )
            }.sortedByDescending { it.groupEntries.sumOf { e -> e.amountCentimos } }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    val globalBalanceState: StateFlow<GlobalBalanceState> = groups
        .map { groupList ->
            val totalQueDebo = groupList.sumOf { group ->
                val balance = group.balancesCentimos[userId] ?: 0L
                if (balance < 0L) -balance else 0L
            }
            val totalQueMeDeben = groupList.sumOf { group ->
                val balance = group.balancesCentimos[userId] ?: 0L
                if (balance > 0L) balance else 0L
            }
            GlobalBalanceState(totalQueDebo, totalQueMeDeben)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), GlobalBalanceState())

    private val _createGroupState = MutableStateFlow<CreateGroupState>(CreateGroupState.Idle)
    val createGroupState: StateFlow<CreateGroupState> = _createGroupState

    private val _addMemberState = MutableStateFlow<AddMemberState>(AddMemberState.Idle)
    val addMemberState: StateFlow<AddMemberState> = _addMemberState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _userSearchState = MutableStateFlow<UserSearchState>(UserSearchState.Idle)
    val userSearchState: StateFlow<UserSearchState> = _userSearchState

    private val _exportEvent = MutableSharedFlow<ExportEvent>()
    val exportEvent: SharedFlow<ExportEvent> = _exportEvent.asSharedFlow()

    private val _groupActionState = MutableStateFlow<GroupActionState>(GroupActionState.Idle)
    val groupActionState: StateFlow<GroupActionState> = _groupActionState

    private val _settlementState = MutableStateFlow<SettlementState>(SettlementState.Idle)
    val settlementState: StateFlow<SettlementState> = _settlementState

    private val _memberProfiles = MutableStateFlow<Map<String, User>>(emptyMap())
    val memberProfiles: StateFlow<Map<String, User>> = _memberProfiles

    private val _joinGroupState = MutableStateFlow<JoinGroupState>(JoinGroupState.Idle)
    val joinGroupState: StateFlow<JoinGroupState> = _joinGroupState

    private var searchJob: Job? = null

    fun createGroup(nombreGrupo: String, creadorId: String, descripcion: String = "", moneda: String = "EUR") {
        viewModelScope.launch {
            _createGroupState.value = CreateGroupState.Loading
            try {
                withTimeout(15_000L) {
                    createGroupUseCase(nombreGrupo, creadorId, descripcion, moneda)
                        .onSuccess {
                            _createGroupState.value = CreateGroupState.Success
                            AnalyticsHelper.logGroupCreated()
                        }
                        .onFailure { e ->
                            Log.e("SplitApp/Groups", "createGroup failure: ${e.message}", e)
                            _createGroupState.value = CreateGroupState.Error(e.message ?: "Error al crear grupo")
                        }
                }
            } catch (e: Exception) {
                Log.e("SplitApp/Groups", "createGroup exception: ${e.message}", e)
                _createGroupState.value = CreateGroupState.Error("Error al crear grupo: ${e.message}")
            }
        }
    }

    fun addMemberByEmail(groupId: String, email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _addMemberState.value = AddMemberState.Loading
            addMemberUseCase(groupId, email)
                .onSuccess { _addMemberState.value = AddMemberState.Success; onSuccess() }
                .onFailure { e ->
                    val msg = e.message ?: "Error al agregar miembro"
                    _addMemberState.value = AddMemberState.Error(msg)
                    onError(msg)
                }
        }
    }

    fun sendInvitation(groupId: String, grupoNombre: String, paraUserId: String) {
        viewModelScope.launch {
            _addMemberState.value = AddMemberState.Loading
            sendInvitationUseCase(
                grupoId = groupId,
                grupoNombre = grupoNombre,
                invitadoPorId = currentUserId,
                invitadoPorNombre = _currentUserName.value,
                paraUserId = paraUserId
            )
                .onSuccess { _addMemberState.value = AddMemberState.Success }
                .onFailure { e ->
                    _addMemberState.value = when (e.message) {
                        "BLOCKED" -> AddMemberState.InvitationBlocked
                        "ALREADY_PENDING" -> AddMemberState.InvitationAlreadyPending
                        "ALREADY_MEMBER" -> AddMemberState.InvitationAlreadyMember
                        else -> AddMemberState.Error(e.message ?: "Error al enviar invitación")
                    }
                }
        }
    }

    fun searchUsers(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) { _userSearchState.value = UserSearchState.Idle; return }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _userSearchState.value = UserSearchState.Loading
            groupRepository.searchUsersByName(query)
                .onSuccess { users ->
                    _userSearchState.value = UserSearchState.Success(
                        users.map { UserSearchResult(it.id, it.nombre, it.email) }
                    )
                }
                .onFailure { _userSearchState.value = UserSearchState.Error(it.message ?: "Error en búsqueda") }
        }
    }

    fun exportGroupExpenses(groupId: String, context: Context) {
        val appContext = context.applicationContext
        viewModelScope.launch {
            _exportEvent.emit(ExportEvent.Loading)
            exportExpensesToCsvUseCase(groupId, appContext.cacheDir)
                .fold(
                    onSuccess = { file ->
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            val result = withContext(Dispatchers.IO) {
                                runCatching {
                                    val values = ContentValues().apply {
                                        put(MediaStore.Downloads.DISPLAY_NAME, file.name)
                                        put(MediaStore.Downloads.MIME_TYPE, "text/csv")
                                    }
                                    val uri = appContext.contentResolver.insert(
                                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                                    ) ?: error("No se pudo crear la entrada en Descargas")
                                    appContext.contentResolver.openOutputStream(uri)?.use { out ->
                                        file.inputStream().use { it.copyTo(out) }
                                    }
                                    file.delete()
                                    file.name
                                }
                            }
                            result.fold(
                                onSuccess = { name -> _exportEvent.emit(ExportEvent.Success(name)) },
                                onFailure = { _exportEvent.emit(ExportEvent.Error(it.message ?: "Error al guardar en Descargas")) }
                            )
                        } else {
                            _exportEvent.emit(ExportEvent.Success(file.absolutePath))
                        }
                    },
                    onFailure = { _exportEvent.emit(ExportEvent.Error(it.message ?: "Error al exportar")) }
                )
        }
    }

    fun isCurrentUserCreator(group: Group): Boolean = group.creadoPor == currentUserId

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            val group = groups.value.firstOrNull { it.id == groupId }
            if (group != null && group.balancesCentimos.values.any { it != 0L }) {
                _groupActionState.value = GroupActionState.Error(
                    "No se puede eliminar el grupo mientras haya deudas pendientes"
                )
                return@launch
            }
            _groupActionState.value = GroupActionState.Loading
            groupRepository.deleteGroup(groupId)
                .onSuccess { _groupActionState.value = GroupActionState.Success }
                .onFailure { _groupActionState.value = GroupActionState.Error(it.message ?: "Error al eliminar") }
        }
    }

    fun leaveGroup(groupId: String) {
        viewModelScope.launch {
            val group = groups.value.firstOrNull { it.id == groupId }
            if (group != null && group.balancesCentimos.values.any { it != 0L }) {
                _groupActionState.value = GroupActionState.Error(
                    "No se puede salir del grupo mientras haya deudas pendientes"
                )
                return@launch
            }
            _groupActionState.value = GroupActionState.Loading
            groupRepository.leaveGroup(groupId, currentUserId)
                .onSuccess { _groupActionState.value = GroupActionState.Success }
                .onFailure { _groupActionState.value = GroupActionState.Error(it.message ?: "Error al salir") }
        }
    }

    fun joinGroupByLink(groupId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            _joinGroupState.value = JoinGroupState.Loading
            groupRepository.joinGroupByDeepLink(groupId, currentUserId)
                .onSuccess { _joinGroupState.value = JoinGroupState.Success; AnalyticsHelper.logGroupJoined(); onSuccess() }
                .onFailure { e ->
                    val msg = e.message ?: "Error al unirse al grupo"
                    _joinGroupState.value = JoinGroupState.Error(msg)
                    onError(msg)
                }
        }
    }

    fun loadMemberProfiles(userIds: List<String>) {
        viewModelScope.launch {
            _memberProfiles.value = groupRepository.getMemberProfiles(userIds)
        }
    }

    fun getOptimizedTransfers(balances: Map<String, Long>): List<Transferencia> =
        simplifyDebtsUseCase(balances)

    fun confirmSettlement(groupId: String) {
        viewModelScope.launch {
            _settlementState.value = SettlementState.Loading
            groupRepository.confirmSettlement(groupId, currentUserId)
                .onSuccess { _settlementState.value = SettlementState.Success }
                .onFailure { _settlementState.value = SettlementState.Error(it.message ?: "Error") }
        }
    }

    fun cancelSettlement(groupId: String) {
        viewModelScope.launch {
            _settlementState.value = SettlementState.Loading
            groupRepository.cancelSettlement(groupId, currentUserId)
                .onSuccess { _settlementState.value = SettlementState.Success }
                .onFailure { _settlementState.value = SettlementState.Error(it.message ?: "Error") }
        }
    }

    fun resetCreateGroupState()  { _createGroupState.value = CreateGroupState.Idle }
    fun resetAddMemberState()    { _addMemberState.value = AddMemberState.Idle }
    fun resetGroupActionState()  { _groupActionState.value = GroupActionState.Idle }
    fun resetJoinGroupState()    { _joinGroupState.value = JoinGroupState.Idle }
    fun resetSearchState()       { _searchQuery.value = ""; _userSearchState.value = UserSearchState.Idle }
    fun resetSettlementState()   { _settlementState.value = SettlementState.Idle }
}
