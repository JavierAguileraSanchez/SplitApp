package com.example.splitapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.splitapp.data.repository.ExpenseRepositoryImpl
import com.example.splitapp.domain.repository.GroupRepository
import com.example.splitapp.domain.repository.InvitationRepository
import com.example.splitapp.domain.usecase.expense.AddExpenseUseCase
import com.example.splitapp.domain.usecase.expense.DeleteExpenseUseCase
import com.example.splitapp.domain.usecase.expense.ExportExpensesToCsvUseCase
import com.example.splitapp.domain.usecase.expense.GetExpensesUseCase
import com.example.splitapp.domain.usecase.expense.SettleDebtUseCase
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
import com.example.splitapp.ui.expense.ExpenseViewModel
import com.example.splitapp.ui.group.GroupDetailScreen
import com.example.splitapp.ui.group.GroupListScreen
import com.example.splitapp.ui.group.GroupViewModel
import com.example.splitapp.ui.group.DebtorsScreen
import com.example.splitapp.ui.invitation.InvitationViewModel
import com.example.splitapp.ui.invitation.InvitationsScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    groupRepository: GroupRepository,
    invitationRepository: InvitationRepository,
    exportExpensesToCsvUseCase: ExportExpensesToCsvUseCase,
    getUserNamesUseCase: GetUserNamesUseCase,
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
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToGroupList = {
                    navController.navigate(Screen.GroupList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onLanguageChange = onLanguageChange
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onNavigateToGroupList = {
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
            GroupDetailScreen(
                groupId = groupId,
                expenseViewModel = expenseViewModel,
                groupViewModel = groupViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
