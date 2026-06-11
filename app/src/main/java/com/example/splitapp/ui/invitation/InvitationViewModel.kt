package com.example.splitapp.ui.invitation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.Invitation
import com.example.splitapp.domain.usecase.invitation.AcceptInvitationUseCase
import com.example.splitapp.domain.usecase.invitation.GetInvitationsUseCase
import com.example.splitapp.domain.usecase.invitation.RejectInvitationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class InvitationViewModel(
    private val getInvitationsUseCase: GetInvitationsUseCase,
    private val acceptInvitationUseCase: AcceptInvitationUseCase,
    private val rejectInvitationUseCase: RejectInvitationUseCase,
    userId: String
) : ViewModel() {

    val invitations: StateFlow<List<Invitation>> = getInvitationsUseCase(userId)
        .catch { emit(emptyList()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), emptyList())

    val pendingCount: StateFlow<Int> = invitations
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000L), 0)

    private val _actionState = MutableStateFlow<InvitationActionState>(InvitationActionState.Idle)
    val actionState: StateFlow<InvitationActionState> = _actionState

    fun accept(invitationId: String, grupoId: String, userId: String) {
        viewModelScope.launch {
            _actionState.value = InvitationActionState.Loading
            acceptInvitationUseCase(invitationId, grupoId, userId)
                .onSuccess { _actionState.value = InvitationActionState.Success }
                .onFailure { _actionState.value = InvitationActionState.Error(it.message ?: "Error al aceptar invitación") }
        }
    }

    fun reject(invitationId: String, grupoId: String, userId: String) {
        viewModelScope.launch {
            _actionState.value = InvitationActionState.Loading
            rejectInvitationUseCase(invitationId, grupoId, userId)
                .onSuccess { _actionState.value = InvitationActionState.Success }
                .onFailure { _actionState.value = InvitationActionState.Error(it.message ?: "Error al rechazar invitación") }
        }
    }

    fun resetActionState() { _actionState.value = InvitationActionState.Idle }
}
