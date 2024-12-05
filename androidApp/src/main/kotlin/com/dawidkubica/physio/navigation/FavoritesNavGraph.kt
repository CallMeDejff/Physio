package com.dawidkubica.physio.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.screens.exercise.ExerciseScreen
import com.dawidkubica.physio.screens.exercise.ExerciseViewModel
import com.dawidkubica.physio.screens.favorites.FavoritesScreen

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.favoritesNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.HOME,
        startDestination = BottomBarScreen.Home.route
    ) {
        composable(route = BottomBarScreen.Home.route) {
            FavoritesScreen(navController = navController)
        }

        composable(route = "exercise_screen/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val viewModel: ExerciseViewModel = hiltViewModel(backStackEntry)
            ExerciseScreen(
                viewModel = viewModel,
                popBackStack = { navController.popBackStack() },
                packageId = exerciseId
            )
        }
    }
}