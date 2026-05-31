package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.data.model.Expense
import com.example.splitapp.domain.repository.ExpenseRepository

class EditExpenseUseCase(private val expenseRepository: ExpenseRepository) {
    suspend operator fun invoke(
        groupId: String,
        oldExpenseId: String,
        newExpense: Expense
    ): Result<Unit> = expenseRepository.editExpense(groupId, oldExpenseId, newExpense)
}
