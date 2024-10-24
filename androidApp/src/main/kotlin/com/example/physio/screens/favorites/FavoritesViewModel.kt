package com.example.physio.screens.favorites

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.UserPackages
import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.StorageService
import com.example.physio.ui.icons.Clinical_notes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val accountService: AccountService,
    private val exercisePackageService: ExercisePackageService,
    private val storageService: StorageService,
) : PhysioAppViewModel() {

    private val _fetchedFavorites = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val fetchedFavorites: StateFlow<List<ExercisePackage>> = _fetchedFavorites

    private val _fetchedAssigned = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val fetchedAssigned: StateFlow<List<ExercisePackage>> = _fetchedAssigned

    private val _fetchedCategories = MutableStateFlow<List<Category>>(emptyList())
    val fetchedCategories: StateFlow<List<Category>> = _fetchedCategories



    fun onAddExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatorWizard.route)
    }

    fun fetchCategories() {
        _fetchedCategories.value = listOf(
            Category("Ulubione pakiety",  Icons.Outlined.FavoriteBorder,"Treść dla kategorii 1", _fetchedFavorites.value),
            Category("Przypisane pakiety",  Clinical_notes,"Treść dla kategorii 2", _fetchedAssigned.value),
            Category("Kategoria 2", Clinical_notes,"Treść dla kategorii 2"),
        )
    }

    fun fetchUserPackages() {
        launchCatching(
            tag = FAVORITES_VIEW_MODEL_TAG,
            block = {
                try {
                    val userPackages = exercisePackageService.getUserExercisePackages()
                    _fetchedFavorites.value = userPackages.favoritePackages as List<ExercisePackage>
                    _fetchedAssigned.value = userPackages.assignedPackages as List<ExercisePackage>
                    Log.d(FAVORITES_VIEW_MODEL_TAG, "Fetched favorite exercise packages: ${userPackages.favoritePackages.size}, assigned packages: ${userPackages.assignedPackages.size}")
                    fetchCategories()
                } catch (e: Exception) {
                    Log.e("FavoritesViewModel", "Error fetching user packages", e)
                }
            }
        )
    }

    companion object {
        private const val FAVORITES_VIEW_MODEL_TAG = "FavoritesViewModel"
    }
}

data class Category(val title: String, val icon : ImageVector, val content: String, val exercisePackages: List<ExercisePackage> = emptyList())