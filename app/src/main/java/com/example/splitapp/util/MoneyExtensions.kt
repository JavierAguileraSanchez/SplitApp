package com.example.splitapp.util

import kotlin.math.roundToLong

fun Long.toEurosString(): String = "%.2f".format(this / 100.0)
fun Long.formatEuros(): String = "${toEurosString()}€"
fun Long.formatMoney(moneda: String): String = when (moneda) {
    "USD" -> "\$${toEurosString()}"
    "GBP" -> "£${toEurosString()}"
    else  -> "${toEurosString()}€"
}
fun Double.toCentimos(): Long = (this * 100).roundToLong()

fun distribuirJusto(totalCentimos: Long, participantes: List<String>): Map<String, Long> {
    if (participantes.isEmpty()) return emptyMap()
    val cuotaBase = totalCentimos / participantes.size
    val resto = totalCentimos % participantes.size
    return participantes.mapIndexed { i, uid ->
        uid to if (i == 0) cuotaBase + resto else cuotaBase
    }.toMap()
}

fun convertirDistribucionACentimos(
    distribucionEuros: Map<String, Double>,
    totalCentimos: Long
): Map<String, Long> {
    if (distribucionEuros.isEmpty()) return emptyMap()
    val participantes = distribucionEuros.keys.toList()
    val rawCentimos = distribucionEuros.mapValues { (_, v) -> (v * 100).roundToLong() }
    val suma = rawCentimos.values.sum()
    val resto = totalCentimos - suma
    return rawCentimos.toMutableMap().also { m ->
        m[participantes.first()] = (m[participantes.first()] ?: 0L) + resto
    }
}
