package com.example.splitapp.ui.expense

import com.example.splitapp.data.model.Expense

sealed interface ExpensesUiState {
    object Loading : ExpensesUiState
    data class Success(val expenses: List<Expense>) : ExpensesUiState
    data class Error(val message: String) : ExpensesUiState
}

sealed class AddExpenseState {
    object Idle : AddExpenseState()
    object Loading : AddExpenseState()
    object Success : AddExpenseState()
    data class Error(val message: String) : AddExpenseState()
}

sealed class SettleDebtState {
    object Idle : SettleDebtState()
    object Loading : SettleDebtState()
    object Success : SettleDebtState()
    data class Error(val message: String) : SettleDebtState()
}
