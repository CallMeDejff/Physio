package com.dawidkubica.physio.screens.wizards.services

import com.dawidkubica.physio.service.services.ListService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

object Validator {
    suspend fun validateFields(
        name: String? = null,
        lastName: String? = null,
        licenseNumber: Int? = null,
        dayOfWeek: String? = null,
        time: String? = null,
        topic: String? = null,
        title: String? = null,
        uniqueTitle: Boolean? = null,
        description: String? = null,
        selectedExercises: List<String>? = null,
        selectedConditions: List<String>? = null,
        selectedEquipment: List<String>? = null,
        nameError: MutableStateFlow<String?>? = null,
        lastNameError: MutableStateFlow<String?>? = null,
        licenseNumberError: MutableStateFlow<String?>? = null,
        dayOfWeekError: MutableStateFlow<String?>? = null,
        timeError: MutableStateFlow<String?>? = null,
        topicError: MutableStateFlow<String?>? = null,
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

        name?.let {
            nameError?.value = if (it.isBlank() || it.length < 3) {
                isValid = false
                val error = "Imię nie może być puste."
                errorMessages.add(error)
                error
            } else null
        }

        lastName?.let {
            lastNameError?.value = if (it.isBlank() || it.length < 3) {
                isValid = false
                val error = "Nazwisko nie może być puste."
                errorMessages.add(error)
                error
            } else null
        }

        licenseNumber?.let {
            licenseNumberError?.value = if (it < 4 || it > 12) {
                isValid = false
                val error = "Sprawdź, czy na pewno podałeś prawidłowy numer."
                errorMessages.add(error)
                error
            } else null
        }

        title?.let {
            titleError?.value = if (it.isBlank()) {
                isValid = false
                val error = "Tytuł nie może być pusty."
                errorMessages.add(error)
                error
            } else null
        }

        dayOfWeek?.let {
            dayOfWeekError?.value = if (it.isBlank()) {
                isValid = false
                val error = "Dzień tygodnia nie może być pusty."
                errorMessages.add(error)
                error
            } else null
        }

        time?.let {
            timeError?.value = if (it.isBlank()) {
                isValid = false
                val error = "Godzina nie może być pusta."
                errorMessages.add(error)
                error
            } else null
        }

        topic?.let {
            topicError?.value = if (it.isBlank()) {
                isValid = false
                val error = "Pakiet ćwiczeń nie może być pusty."
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
