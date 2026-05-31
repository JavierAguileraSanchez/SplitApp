package com.example.splitapp.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.splitapp.data.model.Expense
import com.example.splitapp.domain.usecase.expense.AddExpenseUseCase
import com.example.splitapp.domain.usecase.expense.DeleteExpenseUseCase
import com.example.splitapp.domain.usecase.expense.GetExpensesUseCase
import com.example.splitapp.domain.usecase.expense.SettleDebtUseCase
import com.example.splitapp.domain.usecase.group.GetUserNamesUseCase
import com.example.splitapp.domain.usecase.group.SimplifyDebtsUseCase
import com.example.splitapp.domain.usecase.group.Transferencia
import com.example.splitapp.domain.validator.ExpenseValidator
import com.example.splitapp.util.AnalyticsHelper
import com.example.splitapp.util.convertirDistribucionACentimos
import com.example.splitapp.util.distribuirJusto
import com.example.splitapp.util.toCentimos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID

class ExpenseViewModel(
    private val addExpenseUseCase: AddExpenseUseCase,
    private val getExpensesUseCase: GetExpensesUseCase,
    private val settleDebtUseCase: SettleDebtUseCase,
    private val deleteExpenseUseCase: DeleteExpenseUseCase,
    private val getUserNamesUseCase: GetUserNamesUseCase,
    private val groupId: String
) : ViewModel() {

    private val _expensesState = MutableStateFlow<ExpensesUiState>(ExpensesUiState.Loading)
    val expensesState: StateFlow<ExpensesUiState> = _expensesState

    private val _addExpenseState = MutableStateFlow<AddExpenseState>(AddExpenseState.Idle)
    val addExpenseState: StateFlow<AddExpenseState> = _addExpenseState

    private val _settleDebtState = MutableStateFlow<SettleDebtState>(SettleDebtState.Idle)
    val settleDebtState: StateFlow<SettleDebtState> = _settleDebtState

    private val _settlementTransactions = MutableStateFlow<List<Transferencia>>(emptyList())

    private val simplifyDebtsUseCase = SimplifyDebtsUseCase()

    private val _miembrosNombres = MutableStateFlow<Map<String, String>>(emptyMap())
    val miembrosNombres: StateFlow<Map<String, String>> = _miembrosNombres

    init {
        viewModelScope.launch {
            getExpensesUseCase(groupId)
                .catch { _expensesState.value = ExpensesUiState.Error(it.message ?: "Error desconocido") }
                .collect { _expensesState.value = ExpensesUiState.Success(it) }
        }
    }

    fun loadMiembrosNombres(memberIds: List<String>) {
        viewModelScope.launch {
            _miembrosNombres.value = getUserNamesUseCase(memberIds)
        }
    }

    fun addExpense(
        title: String,
        amount: Double,
        paidBy: String,
        participantUids: List<String>,
        customDistribution: Map<String, Double>? = null
    ) {
        viewModelScope.launch {
            _addExpenseState.value = AddExpenseState.Loading
            try {
                val montoCentimos = amount.toCentimos()
                val opId = UUID.randomUUID().toString()
                val distribucionCentimos = if (customDistribution != null)
                    convertirDistribucionACentimos(customDistribution, montoCentimos)
                else
                    distribuirJusto(montoCentimos, participantUids)

                val validation = ExpenseValidator.validate(
                    concepto             = title,
                    montoCentimos        = montoCentimos,
                    pagadorId            = paidBy,
                    distribucionCentimos = distribucionCentimos,
                    miembrosActivos      = participantUids
                )
                if (validation.isFailure) {
                    _addExpenseState.value = AddExpenseState.Error(
                        validation.exceptionOrNull()?.message ?: "Error de validación"
                    )
                    return@launch
                }

                val expense = Expense(
                    id = opId,
                    concepto = title,
                    montoCentimos = montoCentimos,
                    pagadoPor = paidBy,
                    createdBy = paidBy,
                    distribucionCentimos = distribucionCentimos,
                    clientOperationId = opId,
                    esPersonalizado = customDistribution != null
                )
                addExpenseUseCase(groupId, expense)
                    .onSuccess { _addExpenseState.value = AddExpenseState.Success; AnalyticsHelper.logExpenseAdded() }
                    .onFailure { _addExpenseState.value = AddExpenseState.Error(it.message ?: "Error al añadir gasto") }
            } catch (e: Exception) {
                _addExpenseState.value = AddExpenseState.Error("Error al añadir gasto: ${e.message}")
            }
        }
    }

    internal fun calcularLiquidacionOptima(balancesCentimos: Map<String, Long>): List<Transferencia> {
        val transacciones = simplifyDebtsUseCase(balancesCentimos)
        _settlementTransactions.value = transacciones
        return transacciones
    }

    fun settleDebt() {
        val transfers = _settlementTransactions.value
        if (transfers.isEmpty()) return
        viewModelScope.launch {
            _settleDebtState.value = SettleDebtState.Loading
            settleDebtUseCase(groupId, transfers)
                .onSuccess { _settleDebtState.value = SettleDebtState.Success; AnalyticsHelper.logDebtSettled() }
                .onFailure { _settleDebtState.value = SettleDebtState.Error(it.message ?: "Error al liquidar") }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            deleteExpenseUseCase(groupId, expenseId)
                .onFailure { _expensesState.value = ExpensesUiState.Error(it.message ?: "Error al borrar gasto") }
        }
    }

    fun resetAddExpenseState() { _addExpenseState.value = AddExpenseState.Idle }
    fun resetSettleDebtState() { _settleDebtState.value = SettleDebtState.Idle }
}
