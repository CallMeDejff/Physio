package com.dawidkubica.physio.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.CalendarToday
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
    object CreatorPanel : BottomBarScreen(
        route = "wizards",
        title = "",
        icon = Icons.Outlined.AddCircle,
        icon_focused = Icons.Outlined.AddCircle
    )

    object Home : BottomBarScreen(
        route = "HOME",
        title = "Start",
        icon = Icons.Default.Home,
        icon_focused = Icons.Outlined.Home
    )

    object Search : BottomBarScreen(
        route = "SEARCH",
        title = "Szukaj",
        icon = Icons.Default.Search,
        icon_focused = Icons.Outlined.Search,
    )

    object Scheduler : BottomBarScreen(
        route = "SCHEDULER",
        title = "Harmonogram",
        icon = Icons.Default.CalendarToday,
        icon_focused = Icons.Outlined.CalendarToday,
    )

    object Profile : BottomBarScreen(
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

sealed class ProfileScreen(val route: String) {
    object EditUser : ProfileScreen(route = "edit_user")
    object ChangePasswordReauthorization : ProfileScreen(route = "change_password_reauthorization")
    object ChangePassword : ProfileScreen(route = "change_password")
    object PayWall : ProfileScreen(route = "pay_wall")
}

sealed class CalendarScreen(val route: String) {
    object Calendar : CalendarScreen(route = "calendar")
}

sealed class WizardScreen(val route: String) {
    object CreatorWizard : WizardScreen(route = "wizards")
    object CreateExerciseDetailsScreen : WizardScreen(route = "wizards/create_exercise_details")
    object EditExerciseScreen : WizardScreen(route = "wizards/edit_exercise")
    object EditExerciseDetailsScreen : WizardScreen(route = "wizards/edit_exercise/details")
    object CreatePackage : WizardScreen(route = "wizards/create_package")
    object EditPackage : WizardScreen(route = "wizards/edit_package")
    object EditPackageDetails : WizardScreen(route = "wizards/edit_package/details")
    object AssignPackage : WizardScreen(route = "wizards/assign_package")
}

sealed class AuthScreen(val route: String) {
    object SignIn : AuthScreen(route = "SIGN_IN")
    object SignUp : AuthScreen(route = "SIGN_UP")
    object Splash : AuthScreen(route = "SPLASH_SCREEN")
    object Dashboard : AuthScreen(route = "DASHBOARD_SCREEN")
    object ForgotPassword : AuthScreen(route = "FORGOT")
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val WIZARDS = "wizards_graph"
    const val SEARCH = "search_graph"
    const val PROFILE = "profile_graph"
    const val SCHEDULER = "scheduler_graph"
}