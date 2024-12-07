package com.dawidkubica.physio.screens.wizards.services

import com.dawidkubica.physio.service.services.ListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

object Validator {
    suspend fun validateFields(
        title: String? = null,
        uniqueTitle: Boolean? = null,
        description: String? = null,
        selectedExercises: List<String>? = null,
        selectedConditions: List<String>? = null,
        selectedEquipment: List<String>? = null,
        titleError: MutableStateFlow<String?>? = null,
        descriptionError: MutableStateFlow<String?>? = null,
        conditionError: MutableStateFlow<String?>? = null,
        equipmentError: MutableStateFlow<String?>? = null,
        exerciseError: MutableStateFlow<String?>? = null,
        listService: ListService? = null,
        showMessage: (String) -> Unit = {}
    ): Boolean {
        var isValid = true
        val errorMessages = mutableListOf<String>()

        title?.let {
            titleError?.value = if (it.isBlank()) {
                isValid = false
                val error = "Tytuł nie może być pusty."
                errorMessages.add(error)
                error
            } else null
        }

        if (uniqueTitle == true && !title.isNullOrBlank() && listService != null) {
            val existingTitles = withContext(Dispatchers.IO) {
                listService.getPackagesList(allPackages = true).map { it.second }
            }
            if (existingTitles.contains(title)) {
                isValid = false
                val error = "Tytuł musi być unikalny. Podany tytuł już istnieje."
                errorMessages.add(error)
                titleError?.value = error
            }
        }


        description?.let {
            descriptionError?.value = when {
                it.length < 10 -> {
                    isValid = false
                    val error = "Opis musi mieć co najmniej 10 znaków."
                    errorMessages.add(error)
                    error
                }
                it.length > 800 -> {
                    isValid = false
                    val error = "Opis jest za długi."
                    errorMessages.add(error)
                    error
                }
                else -> null
            }
        }

        selectedConditions?.let {
            conditionError?.value = if (it.isEmpty()) {
                isValid = false
                val error = "Wybierz przynajmniej jedno schorzenie."
                errorMessages.add(error)
                error
            } else null
        }

        selectedEquipment?.let {
            equipmentError?.value = if (it.isEmpty()) {
                isValid = false
                val error = "Wybierz przynajmniej jeden sprzęt."
                errorMessages.add(error)
                error
            } else null
        }

        selectedExercises?.let {
            exerciseError?.value = if (it.isEmpty()) {
                isValid = false
                val error = "Wybierz przynajmniej jedno ćwiczenie."
                errorMessages.add(error)
                error
            } else null
        }

        if (errorMessages.isNotEmpty()) {
            showMessage(errorMessages.joinToString("\n"))
        }

        return isValid
    }
}
