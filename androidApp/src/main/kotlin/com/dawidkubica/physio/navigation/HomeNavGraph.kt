package com.dawidkubica.physio.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dawidkubica.physio.core.navigate
import com.dawidkubica.physio.core.navigateAndPopUp
import com.dawidkubica.physio.core.popUp
import com.dawidkubica.physio.screens.exercise.ExerciseScreen
import com.dawidkubica.physio.screens.exercise.ExerciseViewModel
import com.dawidkubica.physio.screens.favorites.FavoritesScreen
import com.dawidkubica.physio.screens.favorites.FavoritesViewModel
import com.dawidkubica.physio.screens.profile.ProfileScreen
import com.dawidkubica.physio.screens.profile.ProfileViewModel
import com.dawidkubica.physio.screens.reminders.ReminderViewModel
import com.dawidkubica.physio.screens.reminders.SchedulerScreen
import com.dawidkubica.physio.screens.search.SearchScreen
import com.dawidkubica.physio.screens.search.SearchViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) { backStackEntry ->
            val viewModel: FavoritesViewModel = hiltViewModel(backStackEntry)
            FavoritesScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(route = "${BottomBarScreen.Home.route}/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val viewModel: ExerciseViewModel = hiltViewModel(backStackEntry)
            ExerciseScreen(
                viewModel = viewModel,
                popBackStack = { popUp(navController) },
                packageId = exerciseId
            )
        }

        composable(route = BottomBarScreen.Search.route) { backStackEntry ->
            val viewModel: SearchViewModel = hiltViewModel(backStackEntry)
            SearchScreen(
                navigate = { route -> navigate(navController, route) },
                viewModel = viewModel
            )
        }

        composable(route = BottomBarScreen.Scheduler.route) { backStackEntry ->
            val viewModel: ReminderViewModel = hiltViewModel(backStackEntry)
            SchedulerScreen(
                navigate = { route -> navigate(navController, route) },
                viewModel = viewModel
            )
        }

        composable(route = BottomBarScreen.Profile.route) { backStackEntry ->
            val viewModel: ProfileViewModel = hiltViewModel(backStackEntry)
            ProfileScreen(
                openAndPopUp = { route, popUp ->
                    navigateAndPopUp(navController, route, popUp)
                },
                navigate = { route -> navigate(navController, route) },
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
