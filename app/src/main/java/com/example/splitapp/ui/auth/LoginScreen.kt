package com.example.splitapp.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import android.util.Patterns
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.splitapp.R
import com.example.splitapp.util.LocaleManager

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToGroupList: () -> Unit,
    onLanguageChange: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    val context = LocalContext.current
    val currentLanguage = remember { LocaleManager.getLanguage(context) }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthUiState.Success) {
            onNavigateToGroupList()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.logo_splitapp),
                    contentDescription = stringResource(R.string.login_logo_cd),
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 4.dp)
                )

                Text(
                    text = "SplitApp",
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = stringResource(R.string.login_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(R.string.email_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    enabled = authState !is AuthUiState.Loading,
                    isError = email.isNotBlank() && !isEmailValid,
                    supportingText = if (email.isNotBlank() && !isEmailValid) {
                        { Text(stringResource(R.string.login_email_error)) }
                    } else null
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(R.string.login_password_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    enabled = authState !is AuthUiState.Loading
                )

                Spacer(modifier = Modifier.height(24.dp))

                when (authState) {
                    is AuthUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is AuthUiState.Error -> {
                        Text(
                            text = (authState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Button(
                            onClick = { authViewModel.login(email, password) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }

                    is AuthUiState.Success -> {
                        CircularProgressIndicator()
                    }

                    is AuthUiState.Idle -> {
                        Button(
                            onClick = { authViewModel.login(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEmailValid
                        ) {
                            Text(stringResource(R.string.login_button))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.login_register_link),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }

        // Language toggle — top-right corner
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { if (currentLanguage != LocaleManager.LANG_ES) onLanguageChange(LocaleManager.LANG_ES) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.lang_es),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentLanguage == LocaleManager.LANG_ES) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentLanguage == LocaleManager.LANG_ES)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
            Text(
                text = "|",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
            TextButton(
                onClick = { if (currentLanguage != LocaleManager.LANG_EN) onLanguageChange(LocaleManager.LANG_EN) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.lang_en),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (currentLanguage == LocaleManager.LANG_EN) FontWeight.Bold else FontWeight.Normal,
                    color = if (currentLanguage == LocaleManager.LANG_EN)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
        }
    }
}
