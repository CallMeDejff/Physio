package com.dawidkubica.physio.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.screens.change_password.ChangePasswordScreen
import com.dawidkubica.physio.screens.change_password.ChangePasswordViewModel
import com.dawidkubica.physio.screens.edit_user.EditUserScreen
import com.dawidkubica.physio.screens.edit_user.EditUserViewModel

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.PROFILE,
        startDestination = BottomBarScreen.Profile.route
    ) {

        composable(route = ProfileScreen.EditUser.route) { backStackEntry ->
            val viewModel: EditUserViewModel = hiltViewModel(backStackEntry)

            EditUserScreen(
                popBackStack = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        composable(route = ProfileScreen.ChangePasswordReauthorization.route) { backStackEntry ->
            val viewModel: ChangePasswordViewModel = hiltViewModel(backStackEntry)
            ChangePasswordScreen(
                popBackStack = { navController.popBackStack() },
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel,
                reauthentication = true,
            )
        }

        composable(route = ProfileScreen.ChangePassword.route) { backStackEntry ->
            val viewModel: ChangePasswordViewModel = hiltViewModel(backStackEntry)
            ChangePasswordScreen(
                popBackStack = { navController.popBackStack() },
                navigate = { popUp -> navController.navigate(popUp) },
                viewModel = viewModel,
                reauthentication = false,
            )
        }
    }
}