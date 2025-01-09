package com.dawidkubica.physio.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.core.navigate
import com.dawidkubica.physio.core.popUp
import com.dawidkubica.physio.screens.change_password.ChangePasswordScreen
import com.dawidkubica.physio.screens.change_password.ChangePasswordViewModel
import com.dawidkubica.physio.screens.edit_user.EditUserScreen
import com.dawidkubica.physio.screens.edit_user.EditUserViewModel
import com.dawidkubica.physio.screens.profile.PaywallScreen

fun NavGraphBuilder.profileNavGraph(navController: NavHostController) {

    navigation(
        route = Graph.PROFILE,
        startDestination = BottomBarScreen.Profile.route
    ) {

        composable(route = ProfileScreen.EditUser.route) { backStackEntry ->
            val viewModel: EditUserViewModel = hiltViewModel(backStackEntry)
            EditUserScreen(
                popBackStack = { popUp(navController) },
                viewModel = viewModel
            )
        }

        composable(route = ProfileScreen.ChangePasswordReauthorization.route) { backStackEntry ->
            val viewModel: ChangePasswordViewModel = hiltViewModel(backStackEntry)
            ChangePasswordScreen(
                popBackStack = { popUp(navController) },
                navigate = { route -> navigate(navController, route) },
                viewModel = viewModel,
                reauthentication = true,
            )
        }

        composable(route = ProfileScreen.ChangePassword.route) { backStackEntry ->
            val viewModel: ChangePasswordViewModel = hiltViewModel(backStackEntry)
            ChangePasswordScreen(
                popBackStack = { popUp(navController) },
                navigate = { route -> navigate(navController, route) },
                viewModel = viewModel,
                reauthentication = false,
            )
        }

        composable(route = ProfileScreen.PayWall.route) {
            PaywallScreen(
                popBackStack = { popUp(navController) },
            )
        }
    }
}