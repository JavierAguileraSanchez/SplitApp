package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.domain.repository.DebtSettlement
import com.example.splitapp.domain.repository.ExpenseRepository
import com.example.splitapp.domain.usecase.group.Transferencia

class SettleDebtUseCase(private val expenseRepository: ExpenseRepository) {
    suspend operator fun invoke(
        groupId: String,
        transfers: List<Transferencia>
    ): Result<Unit> {
        val settlements = transfers.map {
            DebtSettlement(it.deudor, it.acreedor, it.montoCentimos)
        }
        return expenseRepository.settleDebts(groupId, settlements)
    }
}
