package com.example.physio.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.physio.core.navigateAndPopUp
import com.example.physio.screens.exercise.ExerciseScreen
import com.example.physio.screens.exercise.ExerciseViewModel
import com.example.physio.screens.favorites.FavoritesScreen
import com.example.physio.screens.profile.ProfileScreen
import com.example.physio.screens.profile.ProfileViewModel
import com.example.physio.screens.reminders.ReminderViewModel
import com.example.physio.screens.reminders.SchedulerScreen
import com.example.physio.screens.search.SearchScreen
import com.example.physio.screens.search.SearchViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            FavoritesScreen(navController = navController)
        }

        composable(route = "${BottomBarScreen.Home.route}/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val viewModel: ExerciseViewModel = hiltViewModel(backStackEntry)
            ExerciseScreen(
                navController = navController,
                viewModel = viewModel,
                popBackStack = { navController.popBackStack() },
                packageId = exerciseId
            )
        }

        composable(route = BottomBarScreen.Search.route) { backStackEntry ->
            val viewModel: SearchViewModel = hiltViewModel(backStackEntry)
            SearchScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel
            )
        }

        composable(route = BottomBarScreen.Scheduler.route) { backStackEntry ->
            val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)
            SchedulerScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel
            )
        }

        composable(route = BottomBarScreen.Profile.route) { backStackEntry ->
            val viewModel: ProfileViewModel = hiltViewModel(backStackEntry)
            ProfileScreen(
                openAndPopUp = { route, popUp ->
                    navigateAndPopUp(navController, route, popUp) },
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel
            )
        }

        authNavGraph(navController = navController)
        searchNavGraph(navController = navController)
        wizardsNavGraph(navController = navController)
        profileNavGraph(navController = navController)
        schedulerNavGraph(navController = navController)
    }
}
