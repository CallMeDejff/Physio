package com.dawidkubica.physio.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun rememberPhysioAppState(): PhysioAppState {
    val navController = rememberNavController()
    return remember {
        PhysioAppState(navController)
    }
}

class PhysioAppState(
    val navController: NavHostController
)

fun popUp(navController: NavHostController) {
    navController.popBackStack()
}

fun navigate(navController: NavHostController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
    }
}

fun navigateAndPopUp(navController: NavHostController, route: String, popUp: String) {
    navController.navigate(route) {
        launchSingleTop = true
        popUpTo(popUp) { inclusive = true }
    }
}

fun clearAndNavigate(navController: NavHostController, route: String) {
    navController.navigate(route) {
        launchSingleTop = true
        popUpTo(0) { inclusive = true }
    }
}

