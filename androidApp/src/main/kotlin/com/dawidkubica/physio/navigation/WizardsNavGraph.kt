package com.dawidkubica.physio.navigation

import android.annotation.SuppressLint
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.core.navigate
import com.dawidkubica.physio.core.popUp
import com.dawidkubica.physio.screens.wizards.CreatorWizardScreen
import com.dawidkubica.physio.screens.wizards.exerciseWizard.ExerciseWizardScreen
import com.dawidkubica.physio.screens.wizards.packageWizard.PackageWizardScreen
import com.dawidkubica.physio.screens.wizards.viewmodels.CreatorWizardViewModel
import com.dawidkubica.physio.screens.wizards.viewmodels.ExerciseCreatorViewModel
import com.dawidkubica.physio.screens.wizards.viewmodels.PackageCreatorViewModel

@SuppressLint("StateFlowValueCalledInComposition")
fun NavGraphBuilder.wizardsNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.WIZARDS,
        startDestination = WizardScreen.CreatorWizard.route
    ) {
        composable(route = WizardScreen.CreatorWizard.route) { backStackEntry ->
            val viewModel: CreatorWizardViewModel = hiltViewModel(backStackEntry)
            CreatorWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel
            )
        }

        composable(route = WizardScreen.AssignPackage.route) { backStackEntry ->
            val viewModel: PackageCreatorViewModel = hiltViewModel(backStackEntry)
            PackageWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                assignToPerson = true,
                viewModel = viewModel
            )
        }

        composable(route = WizardScreen.CreatePackage.route) { backStackEntry ->
            val viewModel: PackageCreatorViewModel = hiltViewModel(backStackEntry)
            PackageWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                assignToPerson = false,
                viewModel = viewModel
            )
        }

        composable(route = WizardScreen.CreateExerciseDetailsScreen.route) { backStackEntry ->
            val viewModel: ExerciseCreatorViewModel = hiltViewModel(backStackEntry)
            ExerciseWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel,
                isEditor = false
            )
        }

        composable(route = WizardScreen.EditExerciseScreen.route) { backStackEntry ->
            val viewModel: ExerciseCreatorViewModel = hiltViewModel(backStackEntry)
            ExerciseWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel,
                isEditor = true,
                isEditorNextStep = false
            )
        }

        composable(route = WizardScreen.EditExerciseDetailsScreen.route) { backStackEntry ->
            val viewModel: ExerciseCreatorViewModel =
                if (navController.previousBackStackEntry != null) hiltViewModel(
                    navController.previousBackStackEntry!!
                ) else hiltViewModel()
            ExerciseWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel,
                isEditor = false,
                isEditorNextStep = true
            )
        }

        composable(route = WizardScreen.EditPackage.route) { backStackEntry ->
            val viewModel: PackageCreatorViewModel = hiltViewModel(backStackEntry)
            PackageWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel,
                assignToPerson = false,
                isEditor = true
            )
        }

        composable(route = WizardScreen.EditPackageDetails.route) { backStackEntry ->
            val viewModel: PackageCreatorViewModel =
                if (navController.previousBackStackEntry != null) hiltViewModel(
                    navController.previousBackStackEntry!!
                ) else hiltViewModel()
            PackageWizardScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) },
                viewModel = viewModel,
                assignToPerson = false,
                isEditor = false,
                isEditorNextStep = true
            )
        }
    }
}