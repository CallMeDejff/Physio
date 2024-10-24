package com.example.physio.screens.wizards.viewmodels

import android.util.Log
import com.example.physio.models.User
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ListService
import kotlinx.coroutines.flow.MutableStateFlow

abstract class SharedViewModel : PhysioAppViewModel() {

    protected fun loadConditionList(
        listService: ListService,
        _conditionsList: MutableStateFlow<List<Pair<String, String>>>,
        tag: String
    ) {
        launchCatching(
            tag = tag,
            errorMessage = "Ups! Nie udało się pobrać listy schorzeń.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _conditionsList.value = listService.getConditions()
                Log.d(tag, "loadConditionList: Conditions list loaded, item count: ${_conditionsList.value.size}")
                _isLoading.value = false
            }
        )
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

}
