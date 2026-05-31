package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.domain.repository.ExpenseRepository

class DeleteExpenseUseCase(private val expenseRepository: ExpenseRepository) {
    suspend operator fun invoke(groupId: String, expenseId: String): Result<Unit> =
        expenseRepository.deleteExpense(groupId, expenseId)
}
