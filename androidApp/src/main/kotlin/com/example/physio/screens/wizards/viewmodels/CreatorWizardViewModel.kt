package com.example.physio.screens.wizards.viewmodels

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.navigation.Graph
import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ExerciseService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatorWizardViewModel @Inject constructor(
) : PhysioAppViewModel() {

    fun onNewExerciseClick(navigate: (String) -> Unit) { navigate(WizardScreen.CreateExerciseDetailsScreen.route) }

    fun onEditExerciseClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditExerciseScreen.route) }

    fun onNewPackageClick(navigate: (String) -> Unit) { navigate(WizardScreen.CreatePackage.route) }

    fun onEditPackageWizardClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditPackage.route) }

    fun onAssignPackageClick(navigate: (String) -> Unit) { navigate(WizardScreen.AssignPackage.route) }

    fun onExitWizardClick(navigate: (String) -> Unit) { navigate(Graph.HOME) }

    companion object {
        private const val CREATOR_WIZARD_TAG = "CreatorWizardViewModel"
    }
}