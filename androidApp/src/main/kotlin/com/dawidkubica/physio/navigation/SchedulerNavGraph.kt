package com.dawidkubica.physio.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.core.popUp
import com.dawidkubica.physio.screens.reminders.ReminderViewModel
import com.dawidkubica.physio.screens.reminders.ScheduleReminderScreen

fun NavGraphBuilder.schedulerNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.SCHEDULER,
        startDestination = BottomBarScreen.Scheduler.route
    ) {

        composable(route = CalendarScreen.Calendar.route) { backStackEntry ->
            val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)

            ScheduleReminderScreen(
                popBackStack = { popUp(navController) },
                viewModel = viewModel
            )
        }
    }
}