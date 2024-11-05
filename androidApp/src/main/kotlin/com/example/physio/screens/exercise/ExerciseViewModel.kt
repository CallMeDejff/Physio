package com.example.physio.screens.exercise

import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.models.StorageResult
import com.example.physio.core.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ExerciseService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val accountService: AccountService,
    //private val storageService: StorageService,
    private val exerciseService: ExerciseService,
    private val exercisePackageService: ExercisePackageService
) : PhysioAppViewModel() {

    private val _packageName = MutableStateFlow<String?>("")
    val packageName: StateFlow<String?> = _packageName

    private val _packageId = MutableStateFlow<String?>("")
    val packageId: StateFlow<String?> = _packageId

    private val _packageAuthor = MutableStateFlow<String?>("")
    val packageAuthor: StateFlow<String?> = _packageAuthor

    private val _packageDescription = MutableStateFlow<String?>("")
    val packageDescription: StateFlow<String?> = _packageDescription

    private val _conditionList = MutableStateFlow<List<String>>(emptyList())
    val conditionList: StateFlow<List<String>> = _conditionList

    private val _equipmentList = MutableStateFlow<List<String>>(emptyList())
    val equipmentList: StateFlow<List<String>> = _equipmentList

    private val _warmUpList = MutableStateFlow<List<String>>(emptyList())
    val warmUpList: StateFlow<List<String>> = _warmUpList

    private val _exercisesList = MutableStateFlow<List<String>>(emptyList())
    val exercisesList: StateFlow<List<String>> = _exercisesList

    private val _fetchedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedExercises: StateFlow<List<Exercise>> = _fetchedExercises

    private val _fetchedWarmUps = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedWarmUps: StateFlow<List<Exercise>> = _fetchedWarmUps

    fun getExercisePackage(exercisePackageId: String) {
        launchCatching(
            errorMessage = "Ups! Wystąpił błąd pobierania ćwiczenia",
            onError = { message -> _message.emit(message) },
            tag = EXERCISE_VIEW_MODEL,
            block = {
                _isLoading.update { true }
                val exercisePackage = exercisePackageService.getExercisePackage(exercisePackageId)
                Log.d(
                    EXERCISE_VIEW_MODEL,
                    "getExercisePackage: Received exercise package: $exercisePackage"
                )

                exercisePackage?.let { exPackage ->
                    _packageId.value = exPackage.id
                    _packageName.value = exPackage.name
                    _conditionList.value = exPackage.conditionIds
                    _equipmentList.value = exPackage.equipmentIds
                    _packageDescription.value = exPackage.description
                    _warmUpList.value = exPackage.warmUpIds
                    _exercisesList.value = exPackage.exerciseIds
                }
                val exercises = try {
                    _exercisesList.value.map { exerciseId ->
                        async { exerciseService.getExercise(exerciseId) }
                    }.awaitAll().filterNotNull()
                } catch (e: Exception) {
                    Log.e(EXERCISE_VIEW_MODEL, "Error fetching exercises", e)
                    _isLoading.update { false }
                    emptyList()
                }

                val warmups = try {
                    _warmUpList.value.map { exerciseId ->
                        async { exerciseService.getExercise(exerciseId) }
                    }.awaitAll().filterNotNull()
                } catch (e: Exception) {
                    Log.e(EXERCISE_VIEW_MODEL, "Error fetching warmups", e)
                    _isLoading.update { false }
                    emptyList()
                }
                _fetchedWarmUps.value = warmups
                _fetchedExercises.value = exercises

                Log.d(EXERCISE_VIEW_MODEL, "Fetched exercises: $exercises")
                Log.d(EXERCISE_VIEW_MODEL, "Fetched warmups: $warmups")

                _isLoading.update { false }
            }
        )
    }

    fun togglePackageFavoriteStatus(packageId: String) {
        launchCatching(
            tag = EXERCISE_VIEW_MODEL,
            onError = { message -> _message.emit(message) },
            block = {
                when (val result = accountService.toggleFavoritePackage(packageId)) {
                    is StorageResult.Added -> {
                        Log.d("ViewModel", "Package ${result.packageId} added to favorites")
                        _message.update { "Pakiet dodany do ulubionych" }
                    }

                    is StorageResult.Removed -> {
                        Log.d("ViewModel", "Package ${result.packageId} removed from favorites")
                        _message.update { "Pakiet usunięty z ulubionych" }
                    }

                    is StorageResult.Failure -> {
                        Log.e("ViewModel", "Error: ${result.error.message}")
                        _message.update { "Wystąpił błąd przy dodawaniu do ulubionych" }
                    }
                }
            }
        )
    }


    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        const val EXERCISE_VIEW_MODEL = "ExerciseViewModel"
    }
}