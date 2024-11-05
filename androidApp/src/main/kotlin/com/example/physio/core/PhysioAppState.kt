package com.example.physio.core

import androidx.navigation.NavHostController


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

