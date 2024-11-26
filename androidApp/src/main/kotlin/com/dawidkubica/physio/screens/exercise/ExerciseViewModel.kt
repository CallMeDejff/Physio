package com.dawidkubica.physio.screens.exercise

import android.util.Log
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.models.StorageResult
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.ExerciseService
import com.dawidkubica.physio.service.services.ListService
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
    private val exerciseService: ExerciseService,
    private val exercisePackageService: ExercisePackageService,
    private val listService: ListService
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

    private val _equipmentFullList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentFullList: StateFlow<List<Pair<String, String>>> = _equipmentFullList

    private val _warmUpList = MutableStateFlow<List<String>>(emptyList())
    val warmUpList: StateFlow<List<String>> = _warmUpList

    private val _exercisesList = MutableStateFlow<List<String>>(emptyList())
    val exercisesList: StateFlow<List<String>> = _exercisesList

    private val _fetchedExercises = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedExercises: StateFlow<List<Exercise>> = _fetchedExercises

    private val _fetchedWarmUps = MutableStateFlow<List<Exercise>>(emptyList())
    val fetchedWarmUps: StateFlow<List<Exercise>> = _fetchedWarmUps

    private val _mediaUris = MutableStateFlow<String?>("")
    val mediaUris: StateFlow<String?> = _mediaUris

    private val _isPremium = MutableStateFlow<Boolean?>(false)
    val isPremium: StateFlow<Boolean?> = _isPremium

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
                    _isPremium.value = exPackage.premium
                    _mediaUris.value = exPackage.mediaUrls.firstOrNull().toString()
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
                        Log.d(EXERCISE_VIEW_MODEL, "Package ${result.packageId} added to favorites")
                        _message.update { "Pakiet dodany do ulubionych" }
                    }

                    is StorageResult.Removed -> {
                        Log.d(
                            EXERCISE_VIEW_MODEL,
                            "Package ${result.packageId} removed from favorites"
                        )
                        _message.update { "Pakiet usunięty z ulubionych" }
                    }

                    is StorageResult.Failure -> {
                        Log.e(EXERCISE_VIEW_MODEL, "Error: ${result.error.message}")
                        _message.update { "Wystąpił błąd przy dodawaniu do ulubionych" }
                    }

                    null -> {
                        Log.e(
                            EXERCISE_VIEW_MODEL,
                            "An error occurred while toggling package favorite status"
                        )
                    }
                }
            }
        )
    }

    fun loadEquipmentList() {
        launchCatching(
            tag = EXERCISE_VIEW_MODEL,
            errorMessage = "Ups! Nie udało się pobrać listy sprzętów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _equipmentFullList.value = listService.getEquipments()
                Log.d(
                    EXERCISE_VIEW_MODEL,
                    "loadEquipmentList:Equipment list loaded, item count: ${_equipmentFullList.value.size}"
                )
                _isLoading.value = false

            })
    }

    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        const val EXERCISE_VIEW_MODEL = "ExerciseViewModel"
    }
}