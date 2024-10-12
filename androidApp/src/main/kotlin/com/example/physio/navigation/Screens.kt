package com.example.physio.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomBarScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val icon_focused: ImageVector
) {
    object Home : BottomBarScreen(
        route = "HOME",
        title = "Dashboard",
        icon = Icons.Default.Home,
        icon_focused = Icons.Outlined.Home
    )

    object Search : BottomBarScreen(
        route = "SEARCH",
        title = "Szukaj",
        icon = Icons.Default.Search,
        icon_focused = Icons.Outlined.Search,
    )

    object Profil : BottomBarScreen(
        route = "PROFILE",
        title = "Profil",
        icon = Icons.Default.Person,
        icon_focused = Icons.Outlined.Person,
    )
}

sealed class SearchScreen(val route: String) {
    object ExerciseView : SearchScreen(route = "exercise_screen/{exerciseId}") {
        fun createRoute(exerciseId: String): String = "exercise_screen/$exerciseId"
    }
}


sealed class WizardScreen(val route: String) {
    object CreatorWizard : WizardScreen(route = "wizards")
    object CreateExerciseDetailsScreen : WizardScreen(route = "wizards/create_exercise_details")
    object EditExerciseScreen : WizardScreen(route = "wizards/edit_exercise")
    object EditExerciseDetailsScreen : WizardScreen(route = "wizards/edit_exercise/details")
    object CreatePackage : WizardScreen(route = "wizards/create_package")
    object EditPackage : WizardScreen(route = "wizards/edit_package")
    object EditPackageDetails : WizardScreen(route = "wizards/edit_package/details")
}

sealed class AuthScreen(val route: String) {
    object SignIn : AuthScreen(route = "SIGN_IN")
    object SignUp : AuthScreen(route = "SIGN_UP")
    object Splash : AuthScreen(route = "SPLASH_SCREEN")
    object Dashboard : AuthScreen(route = "DASHBOARD_SCREEN")
    //object Forgot : AuthScreen(route = "FORGOT")
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val WIZARDS = "wizards_graph"
    const val SEARCH = "search_graph"
}