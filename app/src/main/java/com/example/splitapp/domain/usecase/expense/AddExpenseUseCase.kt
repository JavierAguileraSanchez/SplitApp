package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.data.model.Expense
import com.example.splitapp.domain.repository.ExpenseRepository

class AddExpenseUseCase(private val expenseRepository: ExpenseRepository) {
    suspend operator fun invoke(groupId: String, expense: Expense): Result<Unit> {
        return expenseRepository.addExpense(groupId, expense)
    }
}
