package com.dawidkubica.physio.core

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dawidkubica.physio.navigation.DefaultScreen
import com.dawidkubica.physio.navigation.Graph
import com.dawidkubica.physio.navigation.authNavGraph

@Composable
fun PhysioApp(navController: NavHostController) {
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