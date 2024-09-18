package com.example.physio

import ExerciseWizardScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.physio.screens.wizards.exerciseWizard.ExerciseEditorView
import com.example.physio.screens.SearchScreen
import com.example.physio.screens.wizards.CreatorWizardScreen
import com.example.physio.screens.favorites.FavoritesScreen
import com.example.physio.screens.profile.ProfileScreen
import com.example.physio.screens.sign_in.LoginScreen
import com.example.physio.screens.sign_up.SignUpScreen
import com.example.physio.screens.splash.SplashScreen
import com.example.physio.screens.wizards.CreatorWizardViewModel
import com.example.physio.screens.wizards.exerciseWizard.ExerciseWizardEditorScreen
import com.example.physio.ui.PhysioTheme
import com.example.physio.ui.Screens
import com.example.physio.ui.bottomNavigationItems

@Composable
fun PhysioApp() {
    PhysioTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val appState = rememberAppState()

            Scaffold { innerPaddingModifier ->
                NavHost(
                    navController = appState.navController,
                    startDestination = SPLASH_SCREEN,
                    modifier = Modifier.padding(innerPaddingModifier)
                ) {
                    composable(SPLASH_SCREEN) {
                        SplashScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
                    }
                    composable(SIGN_IN_SCREEN) {
                        LoginScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
                    }
                    composable(SIGN_UP_SCREEN) {
                        SignUpScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
                    }
                    composable(DASHBOARD_SCREEN) {
                        BottomNavigationBar()
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar() {
    val navController = rememberNavController()
    val appState = rememberAppState(navController)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    PhysioTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (currentDestination?.route in listOf(
                        Screens.Home.route,
                        Screens.Search.route,
                        Screens.Profile.route,
                        Screens.ExerciseEditor.route
                    )
                ) {
                    NavigationBar {
                        bottomNavigationItems().forEach { navigationItem ->
                            NavigationBarItem(
                                selected = navigationItem.route == currentDestination?.route,
                                label = { Text(navigationItem.label) },
                                icon = {
                                    Icon(
                                        navigationItem.icon,
                                        contentDescription = navigationItem.label
                                    )
                                },
                                onClick = {
                                    navController.navigate(navigationItem.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screens.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Screens.Home.route) {
                    FavoritesScreen(navController = navController, appState = appState)
                }
                composable(Screens.Search.route) {
                    SearchScreen(navController = navController)
                }
                composable(Screens.Profile.route) {
                    ProfileScreen(navController = navController)
                }
                composable(Screens.CreatorWizard.route) {
                    CreatorWizardScreen(
                        navigate = { popUp -> appState.navigate(popUp) },
                        popBackStack = { appState.popUp() }
                    )
                }
                composable(Screens.ExerciseEditor.route) {
                    ExerciseEditorView(
                        //navigate = { popUp -> appState.navigate(popUp) },
                        //initialTitle = "Test title",
                        initialDescription = "Test description"
                    )
                }
                composable(Screens.ExerciseWizard.route) {
                    ExerciseWizardScreen(
                        navigate = { popUp -> appState.navigate(popUp) },
                        popBackStack = { appState.popUp() },
                    )
                }
                composable(Screens.ExerciseWizardEditor.route) {
                    ExerciseWizardEditorScreen(
                        navigate = { popUp -> appState.navigate(popUp) },
                        popBackStack = { appState.popUp() },
                    )
                }
            }
        }
    }
}


@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        PhysioAppState(navController)
    }