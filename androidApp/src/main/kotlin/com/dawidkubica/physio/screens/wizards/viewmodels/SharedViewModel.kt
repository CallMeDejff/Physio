package com.dawidkubica.physio.screens.wizards.viewmodels

import android.util.Log
import com.dawidkubica.physio.core.PhysioAppViewModel
import com.dawidkubica.physio.service.services.ListService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

abstract class SharedViewModel : PhysioAppViewModel() {

    protected suspend fun loadConditionList(
        listService: ListService,
        _conditionsList: MutableStateFlow<List<Pair<String, String>>>,
        _filteredConditionsList: MutableStateFlow<List<Pair<String, String>>> = MutableStateFlow(emptyList()),
        tag: String
    ) {
        try {
            _isLoading.value = true
            val conditions = listService.getConditions()
            _conditionsList.value = conditions
            _filteredConditionsList.value = conditions
            Log.d(tag, "loadConditionList: Conditions list loaded, item count: ${conditions.size}")
        } catch (e: Exception) {
            _message.emit("Ups! Nie udało się pobrać listy schorzeń.")
            Log.e(tag, "Error loading conditions list", e)
        } finally {
            _isLoading.value = false
        }
    }


    protected suspend fun loadBodyPartsList(
        listService: ListService,
        _bodyPartsList: MutableStateFlow<List<Pair<String, String>>>,
        _filteredBodyPartsList: MutableStateFlow<List<Pair<String, String>>> = MutableStateFlow(emptyList()),
        tag: String
    ) {
        try {
            _isLoading.value = true
            val bodyParts = listService.getBodyParts()
            Log.d(tag, "Fetched body parts: $bodyParts")
            _bodyPartsList.value = bodyParts
            _filteredBodyPartsList.value = bodyParts.toList()
            Log.d(tag, "Body parts list updated. Total: ${bodyParts.size}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to load body parts", e)
            _message.emit("Ups! Nie udało się pobrać listy filtrów.")
        } finally {
            _isLoading.value = false
        }
    }

    protected fun loadExercisesList(
        listService: ListService,
        _exercisesList: MutableStateFlow<List<Pair<String, String>>>,
        tag: String
    ) {
        launchCatching(
            tag = tag,
            errorMessage = "Nie udało się pobrać listy ćwiczeń.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _exercisesList.value = listService.getExercises()
                Log.d(tag, "Exercises list loaded, item count: ${_exercisesList.value.size}")
                _isLoading.value = false
            }
        )
    }

    protected fun <T> loadData(
        block: suspend () -> T,
        onSuccess: (T) -> Unit,
        errorMessage: String = "Nie udało się załadować danych.",
        tag: String = ""
    ) {
        launchCatching(
            tag = tag,
            errorMessage = errorMessage,
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                val result = block()
                onSuccess(result)
                _isLoading.value = false
            }
        )
    }

    fun showMessage(message: String) {
        _message.update { message }
    }
}
