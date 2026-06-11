package com.example.splitapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.splitapp.data.repository.AuthRepositoryImpl
import com.example.splitapp.data.repository.ExpenseRepositoryImpl
import com.example.splitapp.data.repository.GroupRepositoryImpl
import com.example.splitapp.data.repository.InvitationRepositoryImpl
import com.example.splitapp.domain.usecase.auth.LoginUseCase
import com.example.splitapp.domain.usecase.auth.RegisterUseCase
import com.example.splitapp.domain.usecase.expense.ExportExpensesToCsvUseCase
import com.example.splitapp.domain.usecase.group.GetUserNamesUseCase
import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.splitapp.ui.auth.AuthViewModel
import com.example.splitapp.ui.navigation.NavGraph
import com.example.splitapp.ui.theme.SplitAppTheme
import com.example.splitapp.util.LocaleManager

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitAppTheme {
                SplitAppNavigation(
                    onLanguageChange = { language ->
                        LocaleManager.setLanguage(this, language)
                        recreate()
                    }
                )
            }
        }
    }
}

@Composable
fun rememberFirebaseAuthUserId(): String {
    val userIdState = remember { mutableStateOf(FirebaseAuth.getInstance().currentUser?.uid ?: "") }
    DisposableEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val listener = FirebaseAuth.AuthStateListener { authState ->
            userIdState.value = authState.currentUser?.uid ?: ""
        }
        auth.addAuthStateListener(listener)
        onDispose {
            auth.removeAuthStateListener(listener)
        }
    }
    return userIdState.value
}

@Composable
fun SplitAppNavigation(onLanguageChange: (String) -> Unit) {
    val navController = rememberNavController()
    val authRepository = remember { AuthRepositoryImpl() }
    val groupRepository = remember { GroupRepositoryImpl() }
    val expenseRepository = remember { ExpenseRepositoryImpl() }
    val invitationRepository = remember { InvitationRepositoryImpl() }
    val exportExpensesToCsvUseCase = remember { ExportExpensesToCsvUseCase(expenseRepository) }
    val getUserNamesUseCase = remember { GetUserNamesUseCase(groupRepository) }
    val authViewModel = viewModel<AuthViewModel> {
        AuthViewModel(LoginUseCase(authRepository), RegisterUseCase(authRepository))
    }

    NavGraph(
        navController = navController,
        authViewModel = authViewModel,
        groupRepository = groupRepository,
        invitationRepository = invitationRepository,
        exportExpensesToCsvUseCase = exportExpensesToCsvUseCase,
        getUserNamesUseCase = getUserNamesUseCase,
        onLanguageChange = onLanguageChange
    )
}
