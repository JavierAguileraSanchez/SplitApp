package com.example.splitapp.ui.group.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.splitapp.domain.usecase.group.Transferencia
import com.example.splitapp.util.formatEuros

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettleDebtDialog(
    transactions: List<Transferencia>,
    userNames: Map<String, String>,
    isLoading: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Liquidar deuda") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Para ponerse al día:",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                if (transactions.isEmpty()) {
                    Text(
                        text = "No hay deudas pendientes.",
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    transactions.forEach { transaction ->
                        val deudorName = userNames[transaction.deudor] ?: transaction.deudor
                        val acreedorName = userNames[transaction.acreedor] ?: transaction.acreedor
                        Text(
                            text = "• $deudorName debe pagar ${transaction.montoCentimos.formatEuros()} a $acreedorName",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                }
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
                if (isLoading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isLoading && transactions.isNotEmpty()) {
                Text("Confirmar Pago")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancelar") }
        }
    )
}
