package com.example.physio.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.physio.screens.SearchScreen
import com.example.physio.screens.favorites.FavoritesScreen
import com.example.physio.screens.profile.ProfileScreen
import com.example.physio.screens.wizards.CreatorWizardScreen
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.screens.wizards.exerciseWizard.ExerciseWizardScreen
import com.example.physio.screens.wizards.packageWizard.PackageWizardScreen

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
        composable(route = BottomBarScreen.Search.route) {
            SearchScreen(navController = navController)
        }
        composable(route = BottomBarScreen.Profil.route) {
            ProfileScreen(navController = navController)
        }
        wizardsNavGraph(navController = navController)
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
fun NavGraphBuilder.wizardsNavGraph(navController: NavHostController) {


    navigation(
        route = Graph.WIZARDS,
        startDestination = WizardScreen.CreatorWizard.route
    ) {
        composable(route = WizardScreen.CreatorWizard.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            CreatorWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(route = WizardScreen.CreatePackage.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            PackageWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(route = WizardScreen.CreateExerciseDetailsScreen.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            ExerciseWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel,
                isEditor = false
            )
        }

        composable(route = WizardScreen.EditExerciseScreen.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            ExerciseWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel,
                isEditor = true,
                isEditorNextStep = false
            )
        }

        composable(route = WizardScreen.EditExerciseDetailsScreen.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel =
                if (navController.previousBackStackEntry != null) hiltViewModel(
                    navController.previousBackStackEntry!!
                ) else hiltViewModel()
            ExerciseWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel,
                isEditor = false,
                isEditorNextStep = true
            )
        }

        composable(route = WizardScreen.EditPackage.route) {  backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            PackageWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel,
                isEditor = true
            )
        }

        composable(route = WizardScreen.EditPackageDetails.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel =
                if (navController.previousBackStackEntry != null) hiltViewModel(
                    navController.previousBackStackEntry!!
                ) else hiltViewModel()
            PackageWizardScreen(
                navigate = { popUp -> navController.navigate(popUp) },
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel,
                isEditor = false,
                isEditorNextStep = true
            )
        }
    }
}
