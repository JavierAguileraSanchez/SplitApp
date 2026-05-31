package com.example.splitapp.domain.validator

object ExpenseValidator {
    fun validate(
        concepto: String,
        montoCentimos: Long,
        pagadorId: String,
        distribucionCentimos: Map<String, Long>,
        miembrosActivos: List<String>
    ): Result<Unit> {
        if (concepto.isBlank())
            return Result.failure(Exception("El concepto no puede estar vacío"))
        if (montoCentimos <= 0L)
            return Result.failure(Exception("El monto debe ser mayor que cero"))
        if (pagadorId !in miembrosActivos)
            return Result.failure(Exception("El pagador debe ser un miembro activo del grupo"))
        if (distribucionCentimos.isEmpty())
            return Result.failure(Exception("Debe haber al menos un participante"))
        if (distribucionCentimos.values.any { it < 0L })
            return Result.failure(Exception("Las cuotas individuales no pueden ser negativas"))
        val suma = distribucionCentimos.values.sum()
        if (suma != montoCentimos)
            return Result.failure(Exception("Error de distribución: ∑cuotas=${suma}¢ ≠ total=${montoCentimos}¢"))
        return Result.success(Unit)
    }
}
