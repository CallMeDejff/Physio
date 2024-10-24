package com.example.physio.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.physio.screens.exercise.ExerciseScreen
import com.example.physio.screens.exercise.ExerciseViewModel
import com.example.physio.screens.favorites.FavoritesScreen
import com.example.physio.screens.search.SearchScreen
import com.example.physio.screens.search.SearchViewModel

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
                navController = navController,
                viewModel = viewModel,
                popBackStack = { navController.popBackStack() },
                packageId = exerciseId
            )
        }
    }
}