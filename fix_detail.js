const fs = require('fs');
const p = process.argv[1];
let c = fs.readFileSync(p, 'utf8');

// ── 1. Add widthIn import ─────────────────────────────────────────────────
if (!c.includes('layout.widthIn')) {
  c = c.replace(
    'import androidx.compose.foundation.layout.width\r\n',
    'import androidx.compose.foundation.layout.width\r\nimport androidx.compose.foundation.layout.widthIn\r\n'
  );
  if (!c.includes('layout.widthIn')) {
    c = c.replace(
      'import androidx.compose.foundation.layout.width\n',
      'import androidx.compose.foundation.layout.width\nimport androidx.compose.foundation.layout.widthIn\n'
    );
  }
}

// ── 2. Add selectedMemberId state variable ────────────────────────────────
c = c.replace(
  'var addMemberError by remember { mutableStateOf("") }
    var selectedMemberId by remember { mutableStateOf("") }',
  'var addMemberError by remember { mutableStateOf("") }\n    var selectedMemberId by remember { mutableStateOf("") }'
);

// ── 3. Update search label ────────────────────────────────────────────────
c = c.replace(
  'label = { Text("Buscar por nombre de usuario") }',
  'label = { Text("Buscar por nombre de usuario") }'
);

// ── 4. Clear selectedMemberId when typing ────────────────────────────────
c = c.replace(
  '                            memberInput = newValue\n                            groupViewModel.searchUsers(newValue)',
  '                            memberInput = newValue\n                            selectedMemberId = ""\n                            groupViewModel.searchUsers(newValue)'
);

// ── 5. Filter out currentUserId from search results ───────────────────────
c = c.replace(
  'val results = (userSearchState as UserSearchState.Success).results\n                                if (results.isNotEmpty()) {',
  'val results = (userSearchState as UserSearchState.Success).results\n                                    .filter { it.id != groupViewModel.currentUserId }\n                                if (results.isNotEmpty()) {'
);

// ── 6. Result click: store nombre + userId ────────────────────────────────
c = c.replace(
  '                                                    .clickable {\n                                                        memberInput = user.email\n                                                        groupViewModel.resetSearchState()\n                                                    }',
  '                                                    .clickable {\n                                                        memberInput = user.nombre\n                                                        selectedMemberId = user.id\n                                                        groupViewModel.resetSearchState()\n                                                    }'
);

// ── 7. Remove email display from result card ──────────────────────────────
c = c.replace(
  '                                                    Text(\n                                                        text = user.nombre,\n                                                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium\n                                                    )\n                                                    Text(\n                                                        text = user.email,\n                                                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,\n                                                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant\n                                                    )',
  '                                                    Text(\n                                                        text = user.nombre,\n                                                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium\n                                                    )'
);

// ── 8. Confirm button: use addMemberById ──────────────────────────────────
c = c.replace(
  '                        if (memberInput.isNotBlank()) {\n                            addMemberError = ""\n                            groupViewModel.addMemberByEmail(\n                                groupId = groupId,\n                                email = memberInput,\n                                onSuccess = {\n                                    showAddMemberDialog = false\n                                    memberInput = ""\n                                    groupViewModel.resetSearchState()\n                                },\n                                onError = { message ->\n                                    addMemberError = message\n                                }\n                            )\n                        }',
  '                        if (selectedMemberId.isNotBlank()) {\n                            addMemberError = ""\n                            groupViewModel.addMemberById(\n                                groupId = groupId,\n                                userId = selectedMemberId,\n                                onSuccess = {\n                                    showAddMemberDialog = false\n                                    memberInput = ""\n                                    selectedMemberId = ""\n                                    groupViewModel.resetSearchState()\n                                },\n                                onError = { message ->\n                                    addMemberError = message\n                                }\n                            )\n                        }'
);

// ── 9. Update enabled: use selectedMemberId ───────────────────────────────
c = c.replace(
  'enabled = selectedMemberId.isNotBlank() && addMemberState !is AddMemberState.Loading',
  'enabled = selectedMemberId.isNotBlank() && addMemberState !is AddMemberState.Loading'
);

// ── 10. Reset selectedMemberId in onDismissRequest ───────────────────────
c = c.replace(
  'showAddMemberDialog = false\n                memberInput = ""\n                groupViewModel.resetAddMemberState()\n                groupViewModel.resetSearchState()',
  'showAddMemberDialog = false\n                memberInput = ""\n                selectedMemberId = ""\n                groupViewModel.resetAddMemberState()\n                groupViewModel.resetSearchState()'
);

// ── 11. Reset selectedMemberId in dismiss TextButton ─────────────────────
c = c.replace(
  '                        showAddMemberDialog = false\n                        memberInput = ""\n                        groupViewModel.resetAddMemberState()\n                        groupViewModel.resetSearchState()',
  '                        showAddMemberDialog = false\n                        memberInput = ""\n                        selectedMemberId = ""\n                        groupViewModel.resetAddMemberState()\n                        groupViewModel.resetSearchState()'
);

// ── 12. BalancesSection: replace Row header with Column + centered button ─
c = c.replace(
  '    Column(modifier = Modifier.fillMaxSize()) {\n        Row(\n            modifier = Modifier\n                .fillMaxWidth()\n                .padding(bottom = 16.dp),\n            horizontalArrangement = Arrangement.SpaceBetween,\n            verticalAlignment = Alignment.CenterVertically\n        ) {\n            Text(text = "Balances actuales", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)\n            Button(onClick = onLiquidarDebt) {\n                Icon(Icons.Default.SwapHoriz, contentDescription = "Liquidar deuda")\n                Spacer(modifier = Modifier.width(8.dp))\n                Text("Liquidar deuda")\n            }\n        }',
  '    Column(modifier = Modifier.fillMaxSize()) {\n        Text(\n            text = "Balances actuales",\n            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,\n            modifier = Modifier.padding(bottom = 12.dp)\n        )\n        Button(\n            onClick = onLiquidarDebt,\n            modifier = Modifier\n                .widthIn(max = 220.dp)\n                .align(Alignment.CenterHorizontally)\n                .padding(bottom = 16.dp)\n        ) {\n            Icon(Icons.Default.SwapHoriz, contentDescription = "Liquidar deuda")\n            Spacer(modifier = Modifier.width(8.dp))\n            Text("Liquidar deuda")\n        }'
);

fs.writeFileSync(p, c, 'utf8');
console.log('GroupDetailScreen OK');