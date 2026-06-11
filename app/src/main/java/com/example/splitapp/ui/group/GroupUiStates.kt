package com.example.splitapp.ui.group

data class GlobalBalanceState(
    val totalQueDebo: Long = 0L,
    val totalQueMeDeben: Long = 0L
)

data class UserSearchResult(val id: String = "", val nombre: String = "", val email: String = "")

sealed class CreateGroupState {
    data object Idle : CreateGroupState()
    data object Loading : CreateGroupState()
    data object Success : CreateGroupState()
    data class Error(val message: String) : CreateGroupState()
}

sealed class AddMemberState {
    data object Idle : AddMemberState()
    data object Loading : AddMemberState()
    data object Success : AddMemberState()
    data class Error(val message: String) : AddMemberState()
    data object InvitationBlocked : AddMemberState()
    data object InvitationAlreadyPending : AddMemberState()
    data object InvitationAlreadyMember : AddMemberState()
}

sealed class GroupActionState {
    data object Idle : GroupActionState()
    data object Loading : GroupActionState()
    data object Success : GroupActionState()
    data class Error(val message: String) : GroupActionState()
}

sealed class JoinGroupState {
    data object Idle : JoinGroupState()
    data object Loading : JoinGroupState()
    data object Success : JoinGroupState()
    data class Error(val message: String) : JoinGroupState()
}

sealed class UserSearchState {
    data object Idle : UserSearchState()
    data object Loading : UserSearchState()
    data class Success(val results: List<UserSearchResult>) : UserSearchState()
    data class Error(val message: String) : UserSearchState()
}

sealed class ExportEvent {
    data object Loading : ExportEvent()
    data class Success(val filePath: String) : ExportEvent()
    data class Error(val message: String) : ExportEvent()
}

sealed class SettlementState {
    data object Idle : SettlementState()
    data object Loading : SettlementState()
    data object Success : SettlementState()
    data class Error(val message: String) : SettlementState()
}
