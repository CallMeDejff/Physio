package com.example.physio.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.physio.screens.editUser.EditUserScreen
import com.example.physio.screens.editUser.EditUserViewModel
import com.example.physio.screens.reminders.ReminderViewModel
import com.example.physio.screens.reminders.ScheduleReminderScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.PROFILE,
        startDestination = BottomBarScreen.Profile.route
    ) {

        composable(route = ProfileScreen.EditUser.route) { backStackEntry ->
            val viewModel: EditUserViewModel = hiltViewModel(backStackEntry)

            EditUserScreen(
                popBackStack = { navController.popBackStack() },
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel
            )
        }

        composable(route = ProfileScreen.ReminderScreen.route) {backStackEntry ->
            val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)

            ScheduleReminderScreen(
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}