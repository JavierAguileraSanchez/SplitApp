package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.data.model.Expense
import com.example.splitapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow

class GetExpensesUseCase(private val expenseRepository: ExpenseRepository) {
    operator fun invoke(groupId: String): Flow<List<Expense>> {
        return expenseRepository.getExpensesForGroup(groupId)
    }
}
