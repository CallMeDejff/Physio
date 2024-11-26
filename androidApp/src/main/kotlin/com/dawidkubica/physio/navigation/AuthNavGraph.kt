package com.dawidkubica.physio.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dawidkubica.physio.core.navigate
import com.dawidkubica.physio.core.navigateAndPopUp
import com.dawidkubica.physio.core.popUp
import com.dawidkubica.physio.screens.forgot_password.ForgotPasswordScreen
import com.dawidkubica.physio.screens.sign_in.LoginScreen
import com.dawidkubica.physio.screens.sign_up.SignUpScreen
import com.dawidkubica.physio.screens.splash.SplashScreen

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
                navigate = { route -> navigate(navController, route) },
                openAndPopUp = { route, popUp ->
                    navigateAndPopUp(navController, route, popUp)
                },
            )
        }

        composable(route = AuthScreen.SignUp.route) {
            SignUpScreen(
                openAndPopUp = { route, popUp ->
                    navigateAndPopUp(
                        navController,
                        route,
                        popUp
                    )
                },
                navController = navController
            )
        }

        composable(route = AuthScreen.ForgotPassword.route) {
            ForgotPasswordScreen(
                navigate = { route -> navigate(navController, route) },
                popBackStack = { popUp(navController) }
            )
        }

        composable(route = AuthScreen.Dashboard.route) {
            navController.navigate(Graph.HOME)
        }

    }
}