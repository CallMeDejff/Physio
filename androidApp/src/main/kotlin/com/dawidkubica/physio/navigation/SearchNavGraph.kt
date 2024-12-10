package com.dawidkubica.physio.navigation

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.core.navigate
import com.dawidkubica.physio.core.popUp
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
                navigate = { route -> navigate(navController, route) },
                viewModel = viewModel
            )
        }

        composable(route = "exercise_screen/{exerciseId}") { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId")
            val viewModel: ExerciseViewModel = hiltViewModel(backStackEntry)
            ExerciseScreen(
                viewModel = viewModel,
                popBackStack = { popUp(navController) },
                packageId = exerciseId
            )
        }
    }
}



