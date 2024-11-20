package com.example.physio.screens.wizards.viewmodels

import com.example.physio.core.PhysioAppViewModel
import com.example.physio.navigation.Graph
import com.example.physio.navigation.WizardScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatorWizardViewModel @Inject constructor(
) : PhysioAppViewModel() {

    fun onNewExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreateExerciseDetailsScreen.route)
    }

    fun onEditExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditExerciseScreen.route)
    }

    fun onNewPackageClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatePackage.route)
    }

    fun onEditPackageWizardClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditPackage.route)
    }

    fun onAssignPackageClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.AssignPackage.route)
    }

    fun onExitWizardClick(navigate: (String) -> Unit) {
        navigate(Graph.HOME)
    }

    companion object {
        private const val CREATOR_WIZARD_TAG = "CreatorWizardViewModel"
    }
}