package com.example.splitapp.ui.invitation

sealed class InvitationActionState {
    data object Idle : InvitationActionState()
    data object Loading : InvitationActionState()
    data object Success : InvitationActionState()
    data class Error(val message: String) : InvitationActionState()
}
