package com.example.physio.screens.wizards.viewmodels

import android.net.Uri
import android.util.Log
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.navigation.WizardScreen
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.AuthenticationService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ExerciseService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class PackageCreatorViewModel @Inject constructor(
    private val exercisePackageService: ExercisePackageService,
    private val listService: ListService,
    private val authenticationService: AuthenticationService,
    private val accountService: AccountService,
    private val exerciseService: ExerciseService
) : SharedViewModel(), DescriptionUpdatable {

    private val _packageName = MutableStateFlow<String?>("")
    val packageName: StateFlow<String?> = _packageName

    private val _packageId = MutableStateFlow<String?>("")
    val packageId: StateFlow<String?> = _packageId

    private val _packageAuthor = MutableStateFlow<String?>("")
    val packageAuthor: StateFlow<String?> = _packageAuthor

    private val _selectedExercises = MutableStateFlow<Set<String>>(emptySet())
    val selectedExercises: StateFlow<Set<String>> = _selectedExercises

    private val _selectedWarmUp = MutableStateFlow<Set<String>>(emptySet())
    val selectedWarmUp: StateFlow<Set<String>> = _selectedWarmUp

    private val _exercisesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exercisesList: StateFlow<List<Pair<String, String>>> = _exercisesList

    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList

    private val _packagesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val packagesList: StateFlow<List<Pair<String, String>>> = _packagesList

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment

    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions

    private val _packageDescription = MutableStateFlow<String?>("")
    val packageDescription: StateFlow<String?> = _packageDescription

    private val _selectedPackages = MutableStateFlow<Set<String>>(emptySet())
    val selectedPackages: StateFlow<Set<String>> = _selectedPackages

    private val _usersList = MutableStateFlow<List<User>>(emptyList())
    val usersList: StateFlow<List<User>> = _usersList

    private val _selectedUsers = MutableStateFlow<Set<String>>(emptySet())
    val selectedUsers: StateFlow<Set<String>> = _selectedUsers

    private val _selectedMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUris: StateFlow<List<Uri>> = _selectedMediaUris

    private val titleError = MutableStateFlow<String?>(null)
    private val descriptionError = MutableStateFlow<String?>(null)
    private val conditionError = MutableStateFlow<String?>(null)
    private val exerciseError = MutableStateFlow<String?>(null)

    fun validateFields(
        title: String,
        description: String,
        selectedExercises: List<String>,
        selectedConditions: List<String>,
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

        descriptionError.value = if (description.length < 10) {
            isValid = false
            val error = "Opis musi mieć co najmniej 10 znaków. "
            errorMessages.add(error)
            error
        } else {
            null
        }

        descriptionError.value = if (description.length > 800) {
            isValid = false
            val error = "Opis jest za długi. "
            errorMessages.add(error)
            error
        } else {
            null
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

    fun addSelectedMedia(uri: Uri) { _selectedMediaUris.update { listOf(uri) } }

    fun removeMediaUri(uri: Uri) { _selectedMediaUris.update { it.filter { existingUri -> existingUri != uri } } }

    fun loadExercises() {
        loadExercisesList(
            _exercisesList = _exercisesList,
            listService = listService,
            tag = PACKAGE_VIEWMODEL_TAG
        )
    }

    fun loadCondition() {
        loadConditionList(
            _conditionsList = _conditionsList,
            listService = listService,
            tag = PACKAGE_VIEWMODEL_TAG
        )
    }

    fun loadPackagesList() {
        loadData(
            block = { listService.getPackagesList(allPackages = false) },
            onSuccess = { _packagesList.value = it },
            errorMessage = "Ups! Nie udało się pobrać listy pakietów."
        )
    }

    fun loadUsersList() {
        loadData(
            block = { accountService.getUsersList() },
            onSuccess = {
                if (it != null) {
                    _usersList.value = it
                }
            },
            errorMessage = "Ups! Nie udało się pobrać listy użytkowników."
        )
    }

    fun onCreatePackageClick(navigate: (String) -> Unit) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching(
            tag = PACKAGE_VIEWMODEL_TAG,
            errorMessage = "Nie udało się utworzyć pakietu.",
            onError = { message -> _message.emit(message) },
            block = {
                val equipmentFromExercises =
                    exerciseService.getEquipmentIdsForExercises(combinedExercises)

                val exercisePackage = ExercisePackage(
                    name = _packageName.value.toString(),
                    exerciseIds = _selectedExercises.value.toList(),
                    warmUpIds = _selectedWarmUp.value.toList(),
                    conditionIds = _selectedConditions.value.toList(),
                    equipmentIds = equipmentFromExercises.values.flatten(),
                    description = _packageDescription.value.toString()
                )
                exercisePackageService.createExercisePackage(
                    exercisePackage,
                    _selectedMediaUris.value
                )
                navigate(WizardScreen.CreatorWizard.route)
                _message.update { "Pakiet ćwiczeń utworzony" }
            })
        navigate(WizardScreen.CreatorWizard.route)
    }

    fun onEditPackageClick(navigate: (String) -> Unit) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching(
            tag = PACKAGE_VIEWMODEL_TAG,
            errorMessage = "Nie udało się zaktualizować pakietu.",
            onError = { message -> _message.emit(message) },
            block = {
                val equipmentFromExercises =
                    exerciseService.getEquipmentIdsForExercises(combinedExercises)
                val exercisePackage = ExercisePackage(
                    id = _packageId.value.toString(),
                    name = _packageName.value.toString(),
                    exerciseIds = _selectedExercises.value.toList(),
                    warmUpIds = _selectedWarmUp.value.toList(),
                    conditionIds = _selectedConditions.value.toList(),
                    equipmentIds = equipmentFromExercises.values.flatten(),
                    description = _packageDescription.value.toString()
                )
                exercisePackageService.updateExercisePackage(
                    exercisePackage,
                    _selectedMediaUris.value
                )

                _message.update { "Pakiet ćwiczeń zaktualizowany" }
                navigate(WizardScreen.CreatorWizard.route)
            })
        navigate(WizardScreen.CreatorWizard.route)
    }

    fun deletePackage(navigate: (String) -> Unit) {
        if (authenticationService.currentUserId == _packageAuthor.value.toString()) {
            launchCatching(
                tag = PACKAGE_VIEWMODEL_TAG,
                errorMessage = "Nie udało się usunąć pakietu.",
                onError = { message -> _message.emit(message) },
                block = {
                    val deletedPackage = ExercisePackage(
                        id = _packageId.value.toString()
                    )
                    exercisePackageService.deleteExercisePackage(deletedPackage)
                }
            )
            navigate(WizardScreen.CreatorWizard.route)
        } else {
            _message.update { "Nie jesteś autorem tego pakietu" }
        }
    }

    fun getPackageDetails() {
        launchCatching(
            tag = PACKAGE_VIEWMODEL_TAG,
            errorMessage = "Nie udało się pobrać szczegółów pakietu.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                val selectedPackageId = _selectedPackages.value.first()
                Log.d(
                    PACKAGE_VIEWMODEL_TAG,
                    "Selected package ID from getPackageDetails: $selectedPackageId"
                )
                val packageDetails = exercisePackageService.getPackage(selectedPackageId)
                packageDetails?.let { exercisePackage ->
                    _packageId.value = exercisePackage.id
                    _packageName.value = exercisePackage.name
                    _packageDescription.value = exercisePackage.description
                    _selectedEquipment.value = exercisePackage.equipmentIds.toSet()
                    _selectedConditions.value = exercisePackage.conditionIds.toSet()
                    _selectedExercises.value = exercisePackage.exerciseIds.toSet()
                    _selectedWarmUp.value = exercisePackage.warmUpIds.toSet()
                    _packageAuthor.value = exercisePackage.uid
                    _selectedMediaUris.value =
                        exercisePackage.mediaUrls.toList().map { Uri.parse(it) }
                }
                Log.d(PACKAGE_VIEWMODEL_TAG, "Package details loaded: ${packageDetails}}")
                _isLoading.value = false
            })
    }

    fun updatePackageName(name: String) { _packageName.value = name }

    fun onAssignPackageClick(navigate: (String) -> Unit) {
        launchCatching(
            tag = PACKAGE_VIEWMODEL_TAG,
            block = {
                Log.d(PACKAGE_VIEWMODEL_TAG, "User ID: ${_selectedUsers.value.first()}")
                Log.d(PACKAGE_VIEWMODEL_TAG, "Package ID: ${_selectedPackages.value.first()}")

                exercisePackageService.assignPackageToUser(
                    userId = _selectedUsers.value.first(),
                    packageId = _selectedPackages.value.first()
                )
                navigate(WizardScreen.CreatorWizard.route)
                _message.update { "Pakiet ćwiczeń został przypisany" }
            }
        )
    }

    override fun updateDescription(description: String) { _packageDescription.value = description }

    fun togglePackage(packageId: String) = toggleItem(
        packageId,
        _selectedPackages,
        allowMultipleSelection = false,
        itemType = "Package"
    )

    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) = toggleItem(
        exerciseId,
        _selectedExercises,
        allowMultipleSelection = multipleSelection,
        itemType = "Exercise"
    )

    fun toggleWarmUp(exerciseId: String) =
        toggleItem(exerciseId, _selectedWarmUp, itemType = "WarmUp")

    fun toggleCondition(conditionId: String, multipleSelection: Boolean) = toggleItem(
        conditionId,
        _selectedConditions,
        allowMultipleSelection = multipleSelection,
        itemType = "Condition"
    )

    fun toggleUser(userId: String) = toggleItem(userId, _selectedUsers, allowMultipleSelection = false, itemType = "User")

    fun onGoBackClick(popBackStack: () -> Unit) { popBackStack() }

    fun onEditPackageContinueClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditPackageDetails.route) }

    companion object {
        private const val PACKAGE_VIEWMODEL_TAG = "PackageViewModel"
    }
}
