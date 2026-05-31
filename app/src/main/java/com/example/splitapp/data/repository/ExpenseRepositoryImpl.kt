package com.example.splitapp.data.repository

import com.example.splitapp.data.model.Expense
import com.example.splitapp.data.model.Group
import com.example.splitapp.domain.repository.DebtSettlement
import com.example.splitapp.domain.repository.ExpenseRepository
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ExpenseRepositoryImpl : FirestoreRepository(), ExpenseRepository {

    override suspend fun addExpense(groupId: String, expense: Expense): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                val groupRef = firestore.collection("grupos").document(groupId)
                val expenseRef = groupRef.collection("gastos").document(expense.clientOperationId)

                firestore.runTransaction { tx ->
                    // IDEMPOTENCIA: si ya existe, salimos sin hacer nada
                    if (tx.get(expenseRef).exists()) return@runTransaction

                    val group = tx.get(groupRef).toObject(Group::class.java)
                        ?: error("Grupo no encontrado")

                    val balances = group.balancesCentimos.toMutableMap()
                    balances[expense.pagadoPor] =
                        (balances[expense.pagadoPor] ?: 0L) + expense.montoCentimos
                    for ((uid, cuota) in expense.distribucionCentimos) {
                        balances[uid] = (balances[uid] ?: 0L) - cuota
                    }

                    check(balances.values.sum() == 0L) {
                        "Violación de invariante ∑ balances ≠ 0 al añadir gasto"
                    }

                    tx.set(expenseRef, expense)
                    tx.update(groupRef, mapOf(
                        "balancesCentimos" to balances,
                        "updatedAt" to FieldValue.serverTimestamp()
                    ))
                }.await()
            }
        }

    override fun getExpensesForGroup(groupId: String): Flow<List<Expense>> = callbackFlow {
        val listener = firestore.collection("grupos").document(groupId)
            .collection("gastos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                if (snapshot != null) trySend(snapshot.toObjects(Expense::class.java))
            }
        awaitClose { listener.remove() }
    }

    override suspend fun settleDebts(
        groupId: String,
        settlements: List<DebtSettlement>
    ): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val groupRef = firestore.collection("grupos").document(groupId)
            firestore.runTransaction { tx ->
                val group = tx.get(groupRef).toObject(Group::class.java)
                    ?: error("Grupo no encontrado")
                val balances = group.balancesCentimos.toMutableMap()

                for ((deudorId, acreedorId, monto) in settlements) {
                    balances[deudorId]   = (balances[deudorId]   ?: 0L) + monto
                    balances[acreedorId] = (balances[acreedorId] ?: 0L) - monto
                }

                check(balances.values.sum() == 0L) {
                    "Violación de invariante ∑ balances ≠ 0 tras liquidación"
                }

                tx.update(groupRef, mapOf(
                    "balancesCentimos" to balances,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
            }.await()
        }
    }

    override suspend fun deleteExpense(groupId: String, expenseId: String): Result<Unit> =
        runCatching {
            withContext(Dispatchers.IO) {
                val groupRef  = firestore.collection("grupos").document(groupId)
                val expenseRef = groupRef.collection("gastos").document(expenseId)

                firestore.runTransaction { tx ->
                    val group = tx.get(groupRef).toObject(Group::class.java)
                        ?: error("Grupo no encontrado")
                    val expense = tx.get(expenseRef).toObject(Expense::class.java)
                        ?: error("Gasto no encontrado")

                    val balances = group.balancesCentimos.toMutableMap()
                    balances[expense.pagadoPor] =
                        (balances[expense.pagadoPor] ?: 0L) - expense.montoCentimos
                    for ((uid, cuota) in expense.distribucionCentimos) {
                        balances[uid] = (balances[uid] ?: 0L) + cuota
                    }

                    check(balances.values.sum() == 0L) {
                        "Violación de invariante ∑ balances ≠ 0 tras borrado"
                    }

                    tx.delete(expenseRef)
                    tx.update(groupRef, mapOf(
                        "balancesCentimos" to balances,
                        "updatedAt" to FieldValue.serverTimestamp()
                    ))
                }.await()
            }
        }

    override suspend fun editExpense(
        groupId: String,
        oldExpenseId: String,
        newExpense: Expense
    ): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val groupRef   = firestore.collection("grupos").document(groupId)
            val oldExpRef  = groupRef.collection("gastos").document(oldExpenseId)
            val newExpRef  = groupRef.collection("gastos").document(newExpense.clientOperationId)

            firestore.runTransaction { tx ->
                val group  = tx.get(groupRef).toObject(Group::class.java)
                    ?: error("Grupo no encontrado")
                val oldExp = tx.get(oldExpRef).toObject(Expense::class.java)
                    ?: error("Gasto original no encontrado")

                val balances = group.balancesCentimos.toMutableMap()

                balances[oldExp.pagadoPor] =
                    (balances[oldExp.pagadoPor] ?: 0L) - oldExp.montoCentimos
                for ((uid, cuota) in oldExp.distribucionCentimos) {
                    balances[uid] = (balances[uid] ?: 0L) + cuota
                }

                balances[newExpense.pagadoPor] =
                    (balances[newExpense.pagadoPor] ?: 0L) + newExpense.montoCentimos
                for ((uid, cuota) in newExpense.distribucionCentimos) {
                    balances[uid] = (balances[uid] ?: 0L) - cuota
                }

                check(balances.values.sum() == 0L) {
                    "Violación de invariante ∑ balances ≠ 0 tras edición"
                }

                tx.delete(oldExpRef)
                tx.set(newExpRef, newExpense)
                tx.update(groupRef, mapOf(
                    "balancesCentimos" to balances,
                    "updatedAt" to FieldValue.serverTimestamp()
                ))
            }.await()
        }
    }

    override suspend fun getExpensesCsv(groupId: String): Result<String> = runCatching {
        withContext(Dispatchers.IO) {
            val expenses = firestore.collection("grupos").document(groupId)
                .collection("gastos")
                .get()
                .await()
                .toObjects(Expense::class.java)

            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.US)
            buildString {
                append("Fecha,Título,Monto (€),Pagado Por,Tipo de División\n")
                for (exp in expenses) {
                    val fecha = try { exp.createdAt?.toDate()?.let { sdf.format(it) } ?: "" } catch (_: Exception) { "" }
                    val titulo = exp.concepto.replace(",", " ")
                    val monto = String.format(java.util.Locale.US, "%.2f", exp.montoCentimos / 100.0)
                    val tipo = if (exp.esPersonalizado) "Personalizada" else "Igual"
                    append("$fecha,$titulo,$monto,${exp.pagadoPor},$tipo\n")
                }
            }
        }
    }
}
