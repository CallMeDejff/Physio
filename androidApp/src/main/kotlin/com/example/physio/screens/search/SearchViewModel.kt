package com.example.physio.screens.search

import android.util.Log
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    //private val storageService: StorageService,
    private val listService: ListService,
    private val exercisePackageService: ExercisePackageService
) : PhysioAppViewModel() {

    private val _isLoadingResults = MutableStateFlow(false)
    val isLoadingResults: StateFlow<Boolean> = _isLoadingResults.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment
    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList

    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions
    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList

    private val _matchingPackages =
        MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
    val matchingPackages: StateFlow<List<Triple<String, String, String>>> = _matchingPackages


    fun loadEquipmentList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy sprzętów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _equipmentList.value = listService.getEquipments()
                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "loadEquipmentList:Equipment list loaded, item count: ${_equipmentList.value.size}"
                )
                _isLoading.value = false

            })
    }

    fun loadConditionList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy schorzeń.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _conditionsList.value = listService.getConditions()
                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "loadConditionList:Conditions list loaded, item count: ${_conditionsList.value.size}"
                )
                _isLoading.value = false
            })
    }

    fun searchForMatchingPackages() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = "Wystąpił błąd podczas wyszukiwania pakietów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoadingResults.value = true
                val conditionIds = _selectedConditions.value.toList()
                val equipmentIds = _selectedEquipment.value.toList()

                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "searchForMatchingPackages: Selected conditionIds: $conditionIds"
                )
                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "searchForMatchingPackages: Selected equipmentIds: $equipmentIds"
                )

                val matchingPackages =
                    exercisePackageService.findMatchingExercisePackages(conditionIds, equipmentIds)

                if (matchingPackages.isNotEmpty()) {
                    _matchingPackages.value = matchingPackages

                    matchingPackages.forEach { (id, name, description) ->
                        Log.d(
                            SEARCH_VIEW_MODEL_TAG,
                            "searchForMatchingPackages: Found package: ID = $id, Name = $name, Description = $description"
                        )
                    }
                    Log.d(
                        SEARCH_VIEW_MODEL_TAG,
                        "searchForMatchingPackages: Packages found: ${matchingPackages.size}"
                    )
                } else {
                    _matchingPackages.value = emptyList()
                    Log.d(
                        SEARCH_VIEW_MODEL_TAG,
                        "searchForMatchingPackages: No matching packages found"
                    )
                    _message.emit("Nie znaleziono żadnych dopasowań")
                }
                _isLoadingResults.value = false
            }
        )
    }

    fun toggleEquipment(equipmentId: String) {
        toggleItem(equipmentId, _selectedEquipment, true,"Equipment", SEARCH_VIEW_MODEL_TAG)
    }

    fun toggleCondition(conditionId: String) {
        toggleItem(conditionId, _selectedConditions, true, "Condition", SEARCH_VIEW_MODEL_TAG)
    }

    companion object {
        private const val SEARCH_VIEW_MODEL_TAG = "SearchViewModel"
    }
}