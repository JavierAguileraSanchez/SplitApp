package com.example.splitapp.ui.group.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.data.model.Group
import com.example.splitapp.util.formatMoney
import kotlin.math.abs

enum class SplitMode { Amounts, Percentages }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    group: Group,
    selectedParticipants: Map<String, Boolean>,
    userNames: Map<String, String>,
    onParticipantToggle: (String) -> Unit,
    pagadorId: String,
    onPagadorChange: (String) -> Unit,
    isCustomSplitEnabled: Boolean,
    onCustomSplitEnabledChange: (Boolean) -> Unit,
    customSplitMode: SplitMode,
    onCustomSplitModeChange: (SplitMode) -> Unit,
    customSplitValues: Map<String, String>,
    onCustomSplitValueChange: (String, String) -> Unit,
    splitValidationMessage: String?,
    titleValue: String,
    onTitleChange: (String) -> Unit,
    amountValue: String,
    onAmountChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    isConfirmEnabled: Boolean,
    moneda: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val participantesActivos = selectedParticipants.filter { it.value }.keys
    val splitAmountLabel = stringResource(R.string.add_expense_split_amount)
    val splitPctLabel = stringResource(R.string.add_expense_split_percentage)
    val splitHint = if (customSplitMode == SplitMode.Percentages) splitPctLabel else splitAmountLabel
    val missingTemplate = stringResource(R.string.add_expense_missing_amount)
    val overTemplate = stringResource(R.string.add_expense_over_amount)
    var pagadorExpanded by remember { mutableStateOf(false) }

    val perParticipantAmounts = participantesActivos.associateWith { memberId ->
        val rawValue = customSplitValues[memberId]?.toDoubleOrNull()
        when {
            isCustomSplitEnabled && customSplitMode == SplitMode.Percentages ->
                (rawValue ?: 0.0) / 100.0 * (amountValue.toDoubleOrNull() ?: 0.0)
            isCustomSplitEnabled -> rawValue ?: 0.0
            participantesActivos.isNotEmpty() ->
                (amountValue.toDoubleOrNull() ?: 0.0) / participantesActivos.size
            else -> 0.0
        }
    }
    val assignedTotal = perParticipantAmounts.values.sum()
    val roundingDifference = (amountValue.toDoubleOrNull() ?: 0.0) - assignedTotal
    val roundingMessage = when {
        abs(roundingDifference) < 0.005 -> null
        roundingDifference > 0 -> String.format(missingTemplate, "${String.format("%.2f", roundingDifference)}€")
        else -> String.format(overTemplate, "${String.format("%.2f", -roundingDifference)}€")
    }
    val miembrosElegibles = group.miembrosActivos

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_expense_title)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = titleValue,
                    onValueChange = onTitleChange,
                    label = { Text(stringResource(R.string.add_expense_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = amountValue,
                    onValueChange = onAmountChange,
                    label = { Text(stringResource(R.string.add_expense_amount_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                ExposedDropdownMenuBox(
                    expanded = pagadorExpanded,
                    onExpandedChange = { if (!isLoading) pagadorExpanded = it }
                ) {
                    OutlinedTextField(
                        value = userNames[pagadorId] ?: pagadorId,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.add_expense_paid_by)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pagadorExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                        enabled = !isLoading
                    )
                    ExposedDropdownMenu(
                        expanded = pagadorExpanded,
                        onDismissRequest = { pagadorExpanded = false }
                    ) {
                        miembrosElegibles.forEach { memberId ->
                            DropdownMenuItem(
                                text = { Text(userNames[memberId] ?: memberId) },
                                onClick = { onPagadorChange(memberId); pagadorExpanded = false },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(R.string.add_expense_custom_split), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = isCustomSplitEnabled,
                        onCheckedChange = onCustomSplitEnabledChange,
                        enabled = !isLoading
                    )
                }
                if (isCustomSplitEnabled) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = stringResource(R.string.add_expense_split_mode), style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = customSplitMode == SplitMode.Amounts,
                            onClick = { onCustomSplitModeChange(SplitMode.Amounts) },
                            enabled = !isLoading
                        )
                        Text(text = splitAmountLabel, modifier = Modifier.padding(start = 4.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = customSplitMode == SplitMode.Percentages,
                            onClick = { onCustomSplitModeChange(SplitMode.Percentages) },
                            enabled = !isLoading
                        )
                        Text(text = "%", modifier = Modifier.padding(start = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.add_expense_enter_value, splitHint),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        participantesActivos.toList().forEach { memberId ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = userNames[memberId] ?: memberId,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = customSplitValues[memberId] ?: "",
                                    onValueChange = { onCustomSplitValueChange(memberId, it) },
                                    label = { Text(splitHint) },
                                    modifier = Modifier.width(120.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    enabled = !isLoading
                                )
                            }
                        }
                    }
                    if (!splitValidationMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = splitValidationMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = stringResource(R.string.add_expense_who_participates), style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Column(modifier = Modifier.fillMaxWidth()) {
                    miembrosElegibles.forEach { memberId ->
                        val checked = selectedParticipants[memberId] == true
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onParticipantToggle(memberId) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { onParticipantToggle(memberId) },
                                enabled = !isLoading
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = userNames[memberId] ?: memberId)
                        }
                    }
                }
                if (participantesActivos.isNotEmpty() && (amountValue.toDoubleOrNull() ?: 0.0) > 0.0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.add_expense_impact_summary),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    perParticipantAmounts.forEach { (memberId, amount) ->
                        Text(
                            text = "• ${userNames[memberId] ?: memberId}: ${(amount * 100).toLong().formatMoney(moneda)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    if (!roundingMessage.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = roundingMessage,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (!errorMessage.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isLoading && isConfirmEnabled) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
