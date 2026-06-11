package com.example.splitapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.example.splitapp.util.ThemeManager

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleManager.applyLocale(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialDark = ThemeManager.getTheme(this)
        setContent {
            val isDarkTheme = remember { mutableStateOf(initialDark) }
            SplitAppTheme(darkTheme = isDarkTheme.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                SplitAppNavigation(
                    isDarkTheme = isDarkTheme.value,
                    onThemeChange = { dark ->
                        isDarkTheme.value = dark
                        ThemeManager.setTheme(this, dark)
                    },
                    onLanguageChange = { language ->
                        LocaleManager.setLanguage(this, language)
                        recreate()
                    }
                )
                } // end Surface
            }
        }
    }
}

@Composable
fun SplitAppNavigation(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
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
        isDarkTheme = isDarkTheme,
        onThemeChange = onThemeChange,
        onLanguageChange = onLanguageChange
    )
}
