package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.data.model.Expense
import com.example.splitapp.util.distribuirJusto
import com.example.splitapp.util.toCentimos
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

data class ImportResult(val importados: Int, val errores: List<String>)

class ImportExpensesFromCsvUseCase(
    private val addExpenseUseCase: AddExpenseUseCase
) {
    suspend operator fun invoke(
        groupId: String,
        csvContent: String,
        currentUserId: String,
        miembrosActivos: List<String>
    ): Result<ImportResult> = withContext(Dispatchers.Default) {
        runCatching {
            if (miembrosActivos.isEmpty()) error("El grupo no tiene miembros activos")

            val lineas = csvContent.lines()
                .drop(1)
                .filter { it.isNotBlank() }

            if (lineas.isEmpty()) error("El fichero no contiene gastos")

            var importados = 0
            val errores = mutableListOf<String>()

            for ((index, linea) in lineas.withIndex()) {
                val fila = index + 2
                try {
                    val cols = linea.split(",")
                    if (cols.size < 3) {
                        errores += "Fila $fila: formato incorrecto (${cols.size} columnas, mínimo 3)"
                        continue
                    }

                    val concepto = cols[1].trim()
                    val montoEuros = cols[2].trim().replace(",", ".").toDoubleOrNull()
                        ?: run { errores += "Fila $fila: monto inválido ('${cols[2]}')"; continue }
                    val pagadoPorRaw = cols.getOrNull(3)?.trim() ?: ""
                    val pagadoPor = if (pagadoPorRaw in miembrosActivos) pagadoPorRaw else currentUserId

                    if (concepto.isBlank()) { errores += "Fila $fila: título vacío"; continue }
                    if (montoEuros <= 0.0) { errores += "Fila $fila: monto debe ser mayor que cero"; continue }

                    val montoCentimos = montoEuros.toCentimos()
                    val opId = UUID.randomUUID().toString()
                    val distribucion = distribuirJusto(montoCentimos, miembrosActivos)

                    val gasto = Expense(
                        id = opId,
                        concepto = concepto,
                        montoCentimos = montoCentimos,
                        pagadoPor = pagadoPor,
                        createdBy = currentUserId,
                        distribucionCentimos = distribucion,
                        clientOperationId = opId
                    )

                    addExpenseUseCase(groupId, gasto)
                        .onSuccess { importados++ }
                        .onFailure { errores += "Fila $fila: ${it.message}" }

                } catch (e: Exception) {
                    errores += "Fila $fila: error inesperado — ${e.message}"
                }
            }

            ImportResult(importados, errores)
        }
    }
}
