package com.example.physio.ui

sealed class Screens(val route : String) {
    object Home : Screens("home_screen")
    object Search : Screens("search_screen")
    object Profile : Screens("profile_screen")
    object ExerciseEditor : Screens("exercise_editor_screen")
    object CreatorWizard: Screens("creator_wizard_screen")
    object ExerciseWizard: Screens("exercise_wizard_screen")
    object ExerciseWizardEditor: Screens("exercise_wizard_creator_screen")
}