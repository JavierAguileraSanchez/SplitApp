package com.example.splitapp.domain.usecase.group

data class Transferencia(val deudor: String, val acreedor: String, val montoCentimos: Long)

class SimplifyDebtsUseCase {
    operator fun invoke(balancesCentimos: Map<String, Long>): List<Transferencia> {
        val deudores = balancesCentimos
            .filter { it.value < 0L }
            .map { it.key to it.value }
            .sortedBy { it.second }
            .toMutableList()

        val acreedores = balancesCentimos
            .filter { it.value > 0L }
            .map { it.key to it.value }
            .sortedByDescending { it.second }
            .toMutableList()

        val resultado = mutableListOf<Transferencia>()

        while (deudores.isNotEmpty() && acreedores.isNotEmpty()) {
            val (dId, dSaldo) = deudores.first()
            val (aId, aSaldo) = acreedores.first()
            val pago = minOf(-dSaldo, aSaldo)

            if (pago <= 0L) break

            resultado += Transferencia(dId, aId, pago)

            val nuevoDSaldo = dSaldo + pago
            val nuevoASaldo = aSaldo - pago

            if (nuevoDSaldo == 0L) deudores.removeFirst() else deudores[0] = dId to nuevoDSaldo
            if (nuevoASaldo == 0L) acreedores.removeFirst() else acreedores[0] = aId to nuevoASaldo
        }

        return resultado
    }
}
