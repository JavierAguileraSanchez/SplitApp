package com.example.splitapp.domain.usecase.expense

import com.example.splitapp.domain.repository.ExpenseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class ExportExpensesToCsvUseCase(
    private val expenseRepository: ExpenseRepository
) {

    suspend operator fun invoke(groupId: String, outputDirectory: File): Result<File> {
        return try {
            val csvResult = expenseRepository.getExpensesCsv(groupId)

            csvResult.fold(
                onSuccess = { csv ->
                    withContext(Dispatchers.IO) {
                        try {
                            if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
                                throw IOException("No se pudo crear el directorio: ${outputDirectory.absolutePath}")
                            }
                            val file = File(outputDirectory, "gastos_${groupId}_${System.currentTimeMillis()}.csv")
                            file.writeText(csv, Charsets.UTF_8)
                            Result.success(file)
                        } catch (e: IOException) {
                            Result.failure(
                                Exception("Permiso denegado al guardar el CSV. Comprueba el almacenamiento del dispositivo. (${e.message})")
                            )
                        }
                    }
                },
                onFailure = { error ->
                    Result.failure(error)
                }
            )
        } catch (e: Exception) {
            Result.failure(Exception("Error al exportar gastos: ${e.message}"))
        }
    }
}