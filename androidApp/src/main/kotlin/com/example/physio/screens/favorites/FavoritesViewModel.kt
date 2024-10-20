package com.example.physio.screens.favorites

import com.example.physio.models.Exercise
import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService,
) : PhysioAppViewModel() {

    private val _fetchedFavorites = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedFavorites: StateFlow<List<Exercise>> = _fetchedFavorites

    private val _fetchedCategories = MutableStateFlow<List<Category>>(emptyList())
    val fetchedCategories: StateFlow<List<Category>> = _fetchedCategories

    fun onAddExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatorWizard.route)
    }

    fun fetchCategories() {
        _fetchedCategories.value = listOf(
            Category("Kategoria 1", "Treść dla kategorii 1"),
            Category("Kategoria 2", "Treść dla kategorii 2"),
            Category("Kategoria 3", "Treść dla kategorii 3")
        )
    }

    fun fetchedFavorites() {

    }
}

data class Category(val title: String, val content: String)