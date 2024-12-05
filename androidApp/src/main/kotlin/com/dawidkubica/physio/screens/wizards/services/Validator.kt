package com.dawidkubica.physio.screens.wizards.services

import kotlinx.coroutines.flow.MutableStateFlow

object Validator {
    fun validateFields(
        title: String,
        description: String,
        selectedExercises: List<String>,
        selectedConditions: List<String>,
        titleError: MutableStateFlow<String?>,
        descriptionError: MutableStateFlow<String?>,
        conditionError: MutableStateFlow<String?>,
        exerciseError: MutableStateFlow<String?>,
        showMessage: (String) -> Unit
    ): Boolean {
        var isValid = true
        val errorMessages = mutableListOf<String>()

        titleError.value = if (title.isBlank()) {
            isValid = false
            val error = "Tytuł nie może być pusty. "
            errorMessages.add(error)
            error
        } else {
            null
        }

        descriptionError.value = when {
            description.length < 10 -> {
                isValid = false
                val error = "Opis musi mieć co najmniej 10 znaków. "
                errorMessages.add(error)
                error
            }
            description.length > 800 -> {
                isValid = false
                val error = "Opis jest za długi. "
                errorMessages.add(error)
                error
            }
            else -> null
        }

        conditionError.value = if (selectedConditions.isEmpty()) {
            isValid = false
            val error = "Wybierz przynajmniej jedno schorzenie. "
            errorMessages.add(error)
            error
        } else {
            null
        }

        exerciseError.value = if (selectedExercises.isEmpty()) {
            isValid = false
            val error = "Wybierz przynajmniej jedno ćwiczenie. "
            errorMessages.add(error)
            error
        } else {
            null
        }

        if (errorMessages.isNotEmpty()) {
            showMessage(errorMessages.joinToString("\n"))
        }

        return isValid
    }
}