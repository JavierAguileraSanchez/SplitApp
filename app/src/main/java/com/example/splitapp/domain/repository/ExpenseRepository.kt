package com.example.splitapp.domain.repository

import com.example.splitapp.data.model.Expense
import kotlinx.coroutines.flow.Flow

data class DebtSettlement(
    val deudorId: String,
    val acreedorId: String,
    val montoCentimos: Long
)

interface ExpenseRepository {
    suspend fun addExpense(groupId: String, expense: Expense): Result<Unit>
    fun getExpensesForGroup(groupId: String): Flow<List<Expense>>
    suspend fun settleDebts(groupId: String, settlements: List<DebtSettlement>): Result<Unit>
    suspend fun deleteExpense(groupId: String, expenseId: String): Result<Unit>
    suspend fun editExpense(groupId: String, oldExpenseId: String, newExpense: Expense): Result<Unit>
    suspend fun getExpensesCsv(groupId: String): Result<String>
}
