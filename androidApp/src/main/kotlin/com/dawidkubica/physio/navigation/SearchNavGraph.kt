package com.dawidkubica.physio.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.screens.exercise.ExerciseScreen
import com.dawidkubica.physio.screens.exercise.ExerciseViewModel
import com.dawidkubica.physio.screens.search.SearchScreen
import com.dawidkubica.physio.screens.search.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.searchNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.SEARCH,
        startDestination = BottomBarScreen.Search.route
    ) {
        composable(route = BottomBarScreen.Search.route) { backStackEntry ->
            val viewModel: SearchViewModel = hiltViewModel(backStackEntry)
            SearchScreen(
                navController = navController,
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel
            )
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



