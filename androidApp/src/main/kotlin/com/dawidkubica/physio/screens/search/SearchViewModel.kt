package com.dawidkubica.physio.screens.search

import android.util.Log
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.navigation.WizardScreen
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val listService: ListService,
    private val exercisePackageService: ExercisePackageService
) : PhysioAppViewModel() {

    private val _isLoadingResults = MutableStateFlow(false)
    val isLoadingResults: StateFlow<Boolean> = _isLoadingResults.asStateFlow()

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment
    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList
    private val _filteredEquipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val filteredEquipmentList: StateFlow<List<Pair<String, String>>> =
        _filteredEquipmentList.asStateFlow()

    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions
    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList
    private val _filteredConditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val filteredConditionsList: StateFlow<List<Pair<String, String>>> =
        _filteredConditionsList.asStateFlow()

    private val _selectedBodyParts = MutableStateFlow<Set<String>>(emptySet())
    val selectedBodyParts: StateFlow<Set<String>> = _selectedBodyParts
    private val _bodyPartsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val bodyPartsList: StateFlow<List<Pair<String, String>>> = _bodyPartsList

    private val _filteredBodyPartsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val filteredBodyPartsList: StateFlow<List<Pair<String, String>>> =
        _filteredBodyPartsList.asStateFlow()

    private val _matchingPackages = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val matchingPackages: StateFlow<List<ExercisePackage>> = _matchingPackages
    private val _filteredPackagesList = MutableStateFlow<List<ExercisePackage>>(emptyList())
    val filteredPackages: StateFlow<List<ExercisePackage>> = _filteredPackagesList.asStateFlow()

    fun onAddExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatorWizard.route)
    }

    fun loadEquipmentList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy sprzętów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _equipmentList.value = listService.getEquipments()
                _filteredEquipmentList.value = _equipmentList.value
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
                _filteredConditionsList.value = _conditionsList.value
                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "loadConditionList:Conditions list loaded, item count: ${_conditionsList.value.size}"
                )
                _isLoading.value = false
            })
    }

    fun loadBodyPartsList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy filtrów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _bodyPartsList.value = listService.getBodyParts()
                _filteredBodyPartsList.value = _bodyPartsList.value
                Log.d(
                    SEARCH_VIEW_MODEL_TAG,
                    "loadBodyPartsList:Conditions list loaded, item count: ${_filteredBodyPartsList.value.size}"
                )
                _isLoading.value = false
            })
    }

    fun filterPackagesList(query: String) {
        _filteredPackagesList.value = if (query.isEmpty()) {
            _matchingPackages.value
        } else {
            _matchingPackages.value.filter { it.name.contains(query, ignoreCase = true) }
        }
    }

    fun filterConditionsList(query: String) {
        _filteredConditionsList.value = if (query.isEmpty()) {
            _conditionsList.value
        } else {
            _conditionsList.value.filter { it.second.contains(query, ignoreCase = true) }
        }
    }

    fun filterBodyPartsList(query: String) {
        _filteredBodyPartsList.value = if (query.isEmpty()) {
            _bodyPartsList.value
        } else {
            _bodyPartsList.value.filter { it.second.contains(query, ignoreCase = true) }
        }
    }

    fun filterEquipmentList(query: String) {
        _filteredEquipmentList.value = if (query.isEmpty()) {
            _equipmentList.value
        } else {
            _equipmentList.value.filter { it.second.contains(query, ignoreCase = true) }
        }
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

                val matchingPackages =
                    exercisePackageService.findMatchingExercisePackages(conditionIds, equipmentIds)

                if (matchingPackages.isNotEmpty()) {
                    _matchingPackages.value = matchingPackages
                    _filteredPackagesList.value = _matchingPackages.value

                    matchingPackages.forEach { pkg ->
                        Log.d(
                            SEARCH_VIEW_MODEL_TAG,
                            "searchForMatchingPackages: Found package: ID = ${pkg.id}, Name = ${pkg.name}, Description = ${pkg.description}, isPremium = ${pkg.premium}"
                        )
                    }
                } else {
                    _matchingPackages.value = emptyList()
                    _filteredPackagesList.value = _matchingPackages.value
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
        toggleItem(equipmentId, _selectedEquipment, true, "Equipment", SEARCH_VIEW_MODEL_TAG)
    }

    fun toggleCondition(conditionId: String) {
        toggleItem(conditionId, _selectedConditions, true, "Condition", SEARCH_VIEW_MODEL_TAG)
    }

    fun toggleBodyPart(bodyPartId: String) {
        toggleItem(bodyPartId, _selectedBodyParts, true, "Body Part", SEARCH_VIEW_MODEL_TAG)
    }

    companion object {
        private const val SEARCH_VIEW_MODEL_TAG = "SearchViewModel"
    }
}