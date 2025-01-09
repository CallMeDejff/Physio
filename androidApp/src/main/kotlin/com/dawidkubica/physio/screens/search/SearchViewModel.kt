package com.dawidkubica.physio.screens.search

import android.content.Context
import com.dawidkubica.physio.R
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val listService: ListService,
    private val exercisePackageService: ExercisePackageService,
    @ApplicationContext private val context: Context
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
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    fun initializeData() {
        _isLoading.update { true }
        fetchData()
        _isLoading.update { false }
    }

    fun refreshData() {
        _isRefreshing.value = true
        fetchData()
        _isRefreshing.value = false
    }

    fun fetchData() {
        loadEquipmentList()
        loadConditionList()
        loadBodyPartsList()
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

    fun loadEquipmentList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.error_load_equipment),
            onError = { message -> _message.emit(message) },
            block = {
                _equipmentList.value = listService.getEquipments()
                _filteredEquipmentList.value = _equipmentList.value
            })
    }

    fun loadConditionList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.error_load_conditions),
            onError = { message -> _message.emit(message) },
            block = {
                _conditionsList.value = listService.getConditions()
                _filteredConditionsList.value = _conditionsList.value
            })
    }

    fun loadBodyPartsList() {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.error_load_body_parts),
            onError = { message -> _message.emit(message) },
            block = {
                _bodyPartsList.value = listService.getBodyParts()
                _filteredBodyPartsList.value = _bodyPartsList.value
            })
    }

    fun searchForMatchingPackages(searchText: String) {
        launchCatching(
            tag = SEARCH_VIEW_MODEL_TAG,
            errorMessage = context.getString(R.string.error_search_packages),
            onError = { message -> _message.emit(message) },
            block = {
                _isLoadingResults.value = true
                val conditionIds = _selectedConditions.value.toList()
                val equipmentIds = _selectedEquipment.value.toList()

                val matchingPackages =
                    exercisePackageService.findMatchingExercisePackages(conditionIds, equipmentIds)

                if (matchingPackages.isNotEmpty()) {
                    _matchingPackages.value = matchingPackages
                    filterPackagesList(searchText) // Automatyczne filtrowanie po wyszukiwaniu
                } else {
                    _matchingPackages.value = emptyList()
                    _filteredPackagesList.value = _matchingPackages.value
                    _message.emit(context.getString(R.string.no_matching_packages))
                }
                _isLoadingResults.value = false
            }
        )
    }

    fun filterPackagesList(query: String) {
        _filteredPackagesList.value = if (query.isEmpty()) {
            _matchingPackages.value
        } else {
            _matchingPackages.value.filter { it.name.contains(query, ignoreCase = true) }
        }
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