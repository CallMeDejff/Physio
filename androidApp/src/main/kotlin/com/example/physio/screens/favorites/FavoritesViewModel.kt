package com.example.physio.screens.favorites

import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
) : PhysioAppViewModel() {

    fun onAddExerciseClick(navigate: (String) -> Unit) {
        //navigate(Screens.ExerciseEditor.route)
        //navigate(Screens.ExerciseWizard.route)
        navigate(WizardScreen.CreatorWizard.route)
    }

}