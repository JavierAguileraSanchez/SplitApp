package com.example.splitapp.ui.group.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.data.model.Group
import com.example.splitapp.ui.components.UserAvatar
import com.example.splitapp.util.formatMoney

@Composable
fun BalancesSection(
    group: Group,
    userNames: Map<String, String>,
    moneda: String,
    currentUserId: String,
    onConfirmSettlement: () -> Unit,
    onCancelSettlement: () -> Unit,
    isSettlementLoading: Boolean
) {
    if (group.miembros.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = stringResource(R.string.balances_no_members), style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    val creditors = group.balancesCentimos.filter { it.value > 0 }.keys.toSet()
    val confirmations = group.liquidacionPendiente?.confirmaciones ?: emptyList()
    val isCreditor = currentUserId in creditors
    val hasConfirmed = currentUserId in confirmations
    val confirmedCount = confirmations.count { it in creditors }
    val totalCreditors = creditors.size

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = stringResource(R.string.balances_current),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(group.miembros) { memberId ->
                    val balance = group.balancesCentimos[memberId] ?: 0L
                    val isInactive = group.estadoMiembros[memberId] == false
                    BalanceItem(
                        memberId   = memberId,
                        balance    = balance,
                        isInactive = isInactive,
                        userNames  = userNames,
                        moneda     = moneda
                    )
                }
            }
        }

        val buttonText = when {
            hasConfirmed -> stringResource(R.string.settle_cancel_confirmation, confirmedCount, totalCreditors)
            isCreditor   -> stringResource(R.string.settle_confirm_progress, confirmedCount, totalCreditors)
            else         -> stringResource(R.string.settle_awaiting, confirmedCount, totalCreditors)
        }

        val isEnabled = isCreditor && !isSettlementLoading && totalCreditors > 0

        ExtendedFloatingActionButton(
            onClick = { if (isEnabled) { if (hasConfirmed) onCancelSettlement() else onConfirmSettlement() } },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .alpha(if (isEnabled) 1f else 0.4f),
            icon = { Icon(Icons.Default.SwapHoriz, contentDescription = null) },
            text = { Text(buttonText) }
        )
    }
}

@Composable
fun BalanceItem(
    memberId: String,
    balance: Long,
    isInactive: Boolean,
    userNames: Map<String, String>,
    moneda: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayName = userNames[memberId] ?: memberId
            UserAvatar(name = displayName, size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = displayName, style = MaterialTheme.typography.titleMedium)
                    if (isInactive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.balance_inactive),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when {
                        balance > 0L -> stringResource(R.string.balance_owed_to_them, balance.formatMoney(moneda))
                        balance < 0L -> stringResource(R.string.balance_owes, (-balance).formatMoney(moneda))
                        else -> stringResource(R.string.balance_neutral)
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = when {
                        balance > 0L -> MaterialTheme.colorScheme.primary
                        balance < 0L -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
