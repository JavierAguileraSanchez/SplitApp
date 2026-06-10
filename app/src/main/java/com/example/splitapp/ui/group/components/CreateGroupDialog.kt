package com.example.splitapp.ui.group.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.ui.group.CreateGroupState

@Composable
fun CreateGroupDialog(
    groupName: String,
    onGroupNameChange: (String) -> Unit,
    groupDescription: String,
    onGroupDescriptionChange: (String) -> Unit,
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
