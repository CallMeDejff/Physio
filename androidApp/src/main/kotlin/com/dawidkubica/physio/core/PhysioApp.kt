package com.dawidkubica.physio.core

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dawidkubica.physio.navigation.DefaultScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.navigation.authNavGraph
import com.dawidkubica.physio.screens.splash.SplashViewModel
import com.dawidkubica.physio.ui.theme.PhysioBarTheme

@Composable
fun PhysioApp(navController: NavHostController) {
    PhysioBarTheme {
        NavHost(
            navController = navController,
            route = Graph.ROOT,
            startDestination = Graph.AUTHENTICATION
        ) {
            authNavGraph(navController = navController)

            composable(route = Graph.HOME) {
                DefaultScreen()
            }
        }
    }
}