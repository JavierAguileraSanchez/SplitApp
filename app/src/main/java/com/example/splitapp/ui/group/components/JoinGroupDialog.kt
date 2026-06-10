package com.example.splitapp.ui.group.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.ui.group.JoinGroupState

@Composable
fun JoinGroupDialog(
    joinLinkInput: String,
    onJoinLinkChange: (String) -> Unit,
    joinError: String,
    joinGroupState: JoinGroupState,
    onConfirm: (groupId: String) -> Unit,
    onDismiss: () -> Unit
) {
    val isLoading = joinGroupState is JoinGroupState.Loading

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(stringResource(R.string.join_group_title)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = joinLinkInput,
                    onValueChange = onJoinLinkChange,
                    label = { Text(stringResource(R.string.join_group_input_label)) },
                    placeholder = { Text(stringResource(R.string.join_group_input_placeholder)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (joinError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = joinError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (isLoading) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val extractedId = if (joinLinkInput.contains("groupId="))
                        joinLinkInput.substringAfter("groupId=").trim()
                    else
                        joinLinkInput.trim()
                    if (extractedId.isNotBlank()) onConfirm(extractedId)
                },
                enabled = joinLinkInput.isNotBlank() && !isLoading
            ) {
                Text(stringResource(R.string.join_group_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
