package com.example.splitapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.splitapp.data.repository.ChatRepositoryImpl
import com.example.splitapp.data.repository.ExpenseRepositoryImpl
import com.example.splitapp.domain.repository.GroupRepository
import com.example.splitapp.domain.repository.InvitationRepository
import com.example.splitapp.domain.usecase.expense.AddExpenseUseCase
import com.example.splitapp.domain.usecase.expense.DeleteExpenseUseCase
import com.example.splitapp.domain.usecase.expense.ExportExpensesToCsvUseCase
import com.example.splitapp.domain.usecase.expense.GetExpensesUseCase
import com.example.splitapp.domain.usecase.expense.SettleDebtUseCase
import com.example.splitapp.domain.usecase.chat.GetMessagesUseCase
import com.example.splitapp.domain.usecase.chat.SendMessageUseCase
import com.example.splitapp.domain.usecase.group.AddMemberUseCase
import com.example.splitapp.domain.usecase.group.CreateGroupUseCase
import com.example.splitapp.domain.usecase.group.GetGroupsUseCase
import com.example.splitapp.domain.usecase.group.GetUserNamesUseCase
import com.example.splitapp.domain.usecase.invitation.AcceptInvitationUseCase
import com.example.splitapp.domain.usecase.invitation.GetInvitationsUseCase
import com.example.splitapp.domain.usecase.invitation.RejectInvitationUseCase
import com.example.splitapp.domain.usecase.invitation.SendInvitationUseCase
import com.example.splitapp.ui.auth.AuthViewModel
import com.example.splitapp.ui.auth.LoginScreen
import com.example.splitapp.ui.auth.RegisterScreen
import com.example.splitapp.ui.chat.ChatViewModel
import com.example.splitapp.ui.expense.ExpenseViewModel
import com.example.splitapp.ui.group.DebtorsScreen
import com.example.splitapp.ui.group.GroupDetailScreen
import com.example.splitapp.ui.group.GroupListScreen
import com.example.splitapp.ui.group.GroupViewModel
import com.example.splitapp.ui.invitation.InvitationViewModel
import com.example.splitapp.ui.invitation.InvitationsScreen
import com.example.splitapp.util.ThemeManager
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    groupRepository: GroupRepository,
    invitationRepository: InvitationRepository,
    exportExpensesToCsvUseCase: ExportExpensesToCsvUseCase,
    getUserNamesUseCase: GetUserNamesUseCase,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit
) {
    val startDestination = if (FirebaseAuth.getInstance().currentUser != null) {
        Screen.GroupList.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            val context = LocalContext.current
            LoginScreen(
                authViewModel = authViewModel,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToGroupList = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val saved = ThemeManager.getUserTheme(context, userId)
                    if (saved == null) {
                        // First time (register path): save selected theme for this user
                        ThemeManager.setUserTheme(context, userId, isDarkTheme)
                    } else if (saved != isDarkTheme) {
                        // Login path: restore user's saved preference
                        onThemeChange(saved)
                    }
                    navController.navigate(Screen.GroupList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLanguageChange = onLanguageChange
            )
        }

        composable(Screen.Register.route) {
            val context = LocalContext.current
            RegisterScreen(
                authViewModel = authViewModel,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToGroupList = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    val saved = ThemeManager.getUserTheme(context, userId)
                    if (saved == null) {
                        ThemeManager.setUserTheme(context, userId, isDarkTheme)
                    } else if (saved != isDarkTheme) {
                        onThemeChange(saved)
                    }
                    navController.navigate(Screen.GroupList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.GroupList.route,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "splitapp://join?groupId={groupId}" }
            )
        ) { backStackEntry ->
            val deepLinkGroupId = backStackEntry.arguments?.getString("groupId")
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val groupViewModel = viewModel<GroupViewModel> {
                GroupViewModel(
                    CreateGroupUseCase(groupRepository),
                    GetGroupsUseCase(groupRepository),
                    AddMemberUseCase(groupRepository),
                    exportExpensesToCsvUseCase,
                    SendInvitationUseCase(invitationRepository),
                    userId = currentUserId,
                    groupRepository = groupRepository
                )
            }
            val invitationViewModel = viewModel<InvitationViewModel> {
                InvitationViewModel(
                    getInvitationsUseCase = GetInvitationsUseCase(invitationRepository),
                    acceptInvitationUseCase = AcceptInvitationUseCase(invitationRepository),
                    rejectInvitationUseCase = RejectInvitationUseCase(invitationRepository),
                    userId = currentUserId
                )
            }
            GroupListScreen(
                groupViewModel = groupViewModel,
                invitationViewModel = invitationViewModel,
                deepLinkGroupId = deepLinkGroupId,
                onNavigateToGroupDetail = { groupId ->
                    navController.navigate(Screen.GroupDetail(groupId).createRoute())
                },
                onNavigateToInvitations = {
                    navController.navigate(Screen.Invitations.route)
                },
                onNavigateToDebtors = {
                    navController.navigate(Screen.Debtors.route)
                },
                onNavigateToLogin = {
                    authViewModel.resetState()
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                onLanguageChange = onLanguageChange
            )
        }

        composable(route = Screen.Invitations.route) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val invitationViewModel = viewModel<InvitationViewModel> {
                InvitationViewModel(
                    getInvitationsUseCase = GetInvitationsUseCase(invitationRepository),
                    acceptInvitationUseCase = AcceptInvitationUseCase(invitationRepository),
                    rejectInvitationUseCase = RejectInvitationUseCase(invitationRepository),
                    userId = currentUserId
                )
            }
            InvitationsScreen(
                invitationViewModel = invitationViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Debtors.route) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val groupViewModel = viewModel<GroupViewModel> {
                GroupViewModel(
                    CreateGroupUseCase(groupRepository),
                    GetGroupsUseCase(groupRepository),
                    AddMemberUseCase(groupRepository),
                    exportExpensesToCsvUseCase,
                    SendInvitationUseCase(invitationRepository),
                    userId = currentUserId,
                    groupRepository = groupRepository
                )
            }
            DebtorsScreen(
                groupViewModel = groupViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.GroupDetail.ROUTE,
            arguments = listOf(
                navArgument("groupId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getString("groupId") ?: ""
            val expenseRepository = remember { ExpenseRepositoryImpl() }
            val expenseViewModel = viewModel<ExpenseViewModel> {
                ExpenseViewModel(
                    addExpenseUseCase    = AddExpenseUseCase(expenseRepository),
                    getExpensesUseCase   = GetExpensesUseCase(expenseRepository),
                    settleDebtUseCase    = SettleDebtUseCase(expenseRepository),
                    deleteExpenseUseCase = DeleteExpenseUseCase(expenseRepository),
                    getUserNamesUseCase  = getUserNamesUseCase,
                    groupId              = groupId
                )
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            val groupViewModel = viewModel<GroupViewModel> {
                GroupViewModel(
                    CreateGroupUseCase(groupRepository),
                    GetGroupsUseCase(groupRepository),
                    AddMemberUseCase(groupRepository),
                    exportExpensesToCsvUseCase,
                    SendInvitationUseCase(invitationRepository),
                    userId = currentUserId,
                    groupRepository = groupRepository
                )
            }
            val chatRepository = remember { ChatRepositoryImpl() }
            val chatViewModel = viewModel<ChatViewModel> {
                ChatViewModel(
                    getMessagesUseCase = GetMessagesUseCase(chatRepository),
                    sendMessageUseCase = SendMessageUseCase(chatRepository),
                    getUserNamesUseCase = getUserNamesUseCase,
                    groupId = groupId,
                    currentUserId = currentUserId
                )
            }
            GroupDetailScreen(
                groupId = groupId,
                expenseViewModel = expenseViewModel,
                groupViewModel = groupViewModel,
                chatViewModel = chatViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
