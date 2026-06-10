package com.example.splitapp.ui.group.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.ui.group.CreateGroupState

private val CURRENCIES = listOf("EUR" to "€ EUR", "USD" to "$ USD", "GBP" to "£ GBP")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupDialog(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    groupDescription: String,
    onGroupDescriptionChange: (String) -> Unit,
    selectedMoneda: String,
    onMonedaChange: (String) -> Unit,
    createGroupState: CreateGroupState,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.create_group_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = onGroupNameChange,
                    label = { Text(stringResource(R.string.create_group_name_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = createGroupState !is CreateGroupState.Loading
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = groupDescription,
                    onValueChange = onGroupDescriptionChange,
                    label = { Text(stringResource(R.string.create_group_description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = createGroupState !is CreateGroupState.Loading,
                    minLines = 2
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.create_group_currency_label),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    CURRENCIES.forEachIndexed { index, (code, label) ->
                        SegmentedButton(
                            selected = selectedMoneda == code,
                            onClick = { onMonedaChange(code) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = CURRENCIES.size),
                            enabled = createGroupState !is CreateGroupState.Loading,
                            label = { Text(label) }
                        )
                    }
                }
                if (createGroupState is CreateGroupState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = createGroupState.message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = createGroupState !is CreateGroupState.Loading && groupName.isNotBlank()
            ) {
                Text(stringResource(R.string.create_group_confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                enabled = createGroupState !is CreateGroupState.Loading
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
