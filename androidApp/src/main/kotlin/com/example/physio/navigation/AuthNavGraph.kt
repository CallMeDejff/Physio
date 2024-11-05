package com.example.physio.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.physio.core.navigateAndPopUp
import com.example.physio.screens.sign_in.LoginScreen
import com.example.physio.screens.sign_up.SignUpScreen
import com.example.physio.screens.splash.SplashScreen

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Splash.route
    ) {
        composable(route = AuthScreen.Splash.route) {
            SplashScreen(openAndPopUp = { route, popUp ->
                navigateAndPopUp(navController, route, popUp)
            })
        }

        composable(route = AuthScreen.SignIn.route) {
            LoginScreen(
                openAndPopUp = { route, popUp ->
                    navigateAndPopUp(navController, route, popUp)
                },
            )
        }

        composable(route = AuthScreen.SignUp.route) {
            SignUpScreen(openAndPopUp = { route, popUp ->
                navigateAndPopUp(
                    navController,
                    route,
                    popUp
                )
            },
                navController = navController
            )
        }

        composable(route = AuthScreen.Dashboard.route) {
            navController.navigate(Graph.HOME)
            //BottomNavigationBar(navController = navController, appState = appState)
        }

    }
}