package com.example.splitapp.ui.group.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.ui.components.UserAvatar
import com.example.splitapp.ui.group.AddMemberState
import com.example.splitapp.ui.group.UserSearchState

@Composable
fun AddMemberDialog(
    currentUserId: String,
    userSearchState: UserSearchState,
    addMemberState: AddMemberState,
    onSearchUsers: (String) -> Unit,
    onAddMember: (userId: String) -> Unit,
    onResetSearch: () -> Unit,
    onResetAddMember: () -> Unit,
    onDismiss: () -> Unit
) {
    var memberInput by remember { mutableStateOf("") }
    var selectedUserId by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = {
            onResetAddMember()
            onResetSearch()
            onDismiss()
        },
        title = { Text(stringResource(R.string.add_member_title)) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = memberInput,
                    onValueChange = { newValue ->
                        memberInput = newValue
                        selectedUserId = ""
                        onSearchUsers(newValue)
                    },
                    label = { Text(stringResource(R.string.add_member_search_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = addMemberState !is AddMemberState.Loading
                )

                if (memberInput.isNotBlank() && userSearchState !is UserSearchState.Idle) {
                    Spacer(modifier = Modifier.height(12.dp))
                    when (userSearchState) {
                        is UserSearchState.Loading -> {
                            CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                        }
                        is UserSearchState.Success -> {
                            val results = userSearchState.results.filter { it.id != currentUserId }
                            if (results.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                ) {
                                    items(results) { user ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    memberInput = user.nombre
                                                    selectedUserId = user.id
                                                    onResetSearch()
                                                }
                                                .padding(4.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                UserAvatar(name = user.nombre, size = 32.dp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(
                                                    text = user.nombre,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                            }
                                        }
                                    }
                                }
                            } else {
                                Text(
                                    text = stringResource(R.string.add_member_no_results),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        is UserSearchState.Error -> {
                            Text(
                                text = userSearchState.message,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                        is UserSearchState.Idle -> {}
                    }
                }

                if (addMemberState is AddMemberState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = addMemberState.message,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (addMemberState is AddMemberState.Loading) {
                    Spacer(modifier = Modifier.height(12.dp))
                    CircularProgressIndicator(modifier = Modifier.width(24.dp).height(24.dp))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (selectedUserId.isNotBlank()) onAddMember(selectedUserId) },
                enabled = selectedUserId.isNotBlank() && addMemberState !is AddMemberState.Loading
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onResetAddMember()
                    onResetSearch()
                    onDismiss()
                },
                enabled = addMemberState !is AddMemberState.Loading
            ) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
