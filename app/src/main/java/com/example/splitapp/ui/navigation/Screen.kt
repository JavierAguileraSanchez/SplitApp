package com.example.splitapp.ui.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object GroupList : Screen("groupList")
    data class GroupDetail(val groupId: String) : Screen("groupDetail/{groupId}") {
        fun createRoute() = "groupDetail/$groupId"
        companion object { const val ROUTE = "groupDetail/{groupId}" }
    }
}
