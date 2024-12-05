package com.dawidkubica.physio.screens.wizards.viewmodels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.User
import com.dawidkubica.physio.navigation.WizardScreen
import com.dawidkubica.physio.screens.wizards.services.MediaProcessor
import com.dawidkubica.physio.screens.wizards.services.Validator
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.AuthenticationService
import com.dawidkubica.physio.service.services.ExercisePackageService
import com.dawidkubica.physio.service.services.ExerciseService
import com.dawidkubica.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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

    fun addSelectedMedia(uri: Uri) {
        _selectedMediaUris.update { listOf(uri) }
    }

    fun removeMediaUri(uri: Uri) {
        _selectedMediaUris.update { it.filter { existingUri -> existingUri != uri } }
    }

    fun loadExercises() {
        loadExercisesList(
            _exercisesList = _exercisesList,
            listService = listService,
            tag = PACKAGE_VIEWMODEL_TAG
        )
    }

    fun loadBodyPartsList() {
        viewModelScope.launch {
            Log.d(PACKAGE_VIEWMODEL_TAG, "Loading body parts...")
            loadBodyPartsList(
                listService = listService,
                _bodyPartsList = _bodyPartsList,
                _filteredBodyPartsList = _filteredBodyPartsList,
                tag = PACKAGE_VIEWMODEL_TAG
            )
            Log.d(
                PACKAGE_VIEWMODEL_TAG,
                "Body parts loaded. BodyParts: ${_bodyPartsList.value}, Filtered: ${_filteredBodyPartsList.value}"
            )
        }
    }

    fun loadCondition() {
        viewModelScope.launch {
            loadConditionList(
                listService = listService,
                _conditionsList = _conditionsList,
                _filteredConditionsList = _filteredConditionsList,
                tag = PACKAGE_VIEWMODEL_TAG
            )
            Log.d(PACKAGE_VIEWMODEL_TAG, "Conditions list loaded: ${_filteredConditionsList.value}")
        }
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
                    _selectedBodyParts.value = exercisePackage.bodyPartIds.toSet()
                    _packageAuthor.value = exercisePackage.uid
                    _selectedMediaUris.value =
                        exercisePackage.mediaUrls.toList().map { Uri.parse(it) }
                }
                Log.d(PACKAGE_VIEWMODEL_TAG, "Package details loaded: ${packageDetails}}")
                _isLoading.value = false
            })
    }

    fun updatePackageName(name: String) {
        _packageName.value = name
    }

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

    override fun updateDescription(description: String) {
        _packageDescription.value = description
    }

    fun togglePackage(packageId: String) = toggleItem(
        packageId,
        _selectedPackages,
        allowMultipleSelection = false,
        itemType = "Package",
        tag = PACKAGE_VIEWMODEL_TAG
    )

    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) = toggleItem(
        exerciseId,
        _selectedExercises,
        allowMultipleSelection = multipleSelection,
        itemType = "Exercise",
        tag = PACKAGE_VIEWMODEL_TAG
    )

    fun toggleWarmUp(exerciseId: String) =
        toggleItem(exerciseId, _selectedWarmUp, itemType = "WarmUp", tag = PACKAGE_VIEWMODEL_TAG)

    fun toggleCondition(conditionId: String, multipleSelection: Boolean) = toggleItem(
        conditionId,
        _selectedConditions,
        allowMultipleSelection = multipleSelection,
        itemType = "Condition",
        tag = PACKAGE_VIEWMODEL_TAG
    )

    fun toggleBodyPart(bodyPartId: String, multipleSelection: Boolean) = toggleItem(
        bodyPartId,
        _selectedBodyParts,
        allowMultipleSelection = multipleSelection,
        itemType = "BodyPart",
        tag = PACKAGE_VIEWMODEL_TAG
    )

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

    fun toggleUser(userId: String) =
        toggleItem(userId, _selectedUsers, allowMultipleSelection = false, itemType = "User")

    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    fun onEditPackageContinueClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditPackageDetails.route)
    }

    private fun onPackageOperationClick(
        navigate: (String) -> Unit,
        context: Context,
        isEdit: Boolean
    ) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()
        val title = _packageName.value.orEmpty()
        val description = _packageDescription.value.orEmpty()
        val selectedConditions = _selectedConditions.value.toList()
        val selectedExercises = _selectedExercises.value.toList()

        if (!Validator.validateFields(
                title,
                description,
                selectedExercises,
                selectedConditions,
                titleError,
                descriptionError,
                conditionError,
                exerciseError,
                showMessage = { message -> _message.update { message } }
            )
        ) {
            return
        }

        if (_selectedMediaUris.value.isNotEmpty()) {
            _selectedMediaUris.value.forEach { uri ->
                validateAndProcessMedia(
                    uri = uri,
                    context = context,
                    onError = { errorMessage ->
                        _message.update { errorMessage }
                    },
                    onSuccess = { processedUri ->
                        handlePackageSave(navigate, processedUri, combinedExercises, isEdit)
                    }
                )
            }
        } else {
            handlePackageSave(navigate, null, combinedExercises, isEdit)
        }
    }

    fun onCreatePackageClick(navigate: (String) -> Unit, context: Context) {
        onPackageOperationClick(navigate, context, isEdit = false)
    }

    fun onEditPackageClick(navigate: (String) -> Unit, context: Context) {
        onPackageOperationClick(navigate, context, isEdit = true)
    }

    private fun handlePackageSave(
        navigate: (String) -> Unit,
        processedUri: Uri?,
        combinedExercises: List<String>,
        isEdit: Boolean
    ) {
        launchCatching(
            tag = PACKAGE_VIEWMODEL_TAG,
            errorMessage = if (isEdit) "Nie udało się zaktualizować pakietu." else "Nie udało się utworzyć pakietu.",
            onError = { message -> _message.emit(message) },
            block = {
                val exercisePackage = prepareExercisePackage(processedUri, combinedExercises)
                if (isEdit) {
                    exercisePackageService.updateExercisePackage(
                        exercisePackage,
                        processedUri?.let { listOf(it) } ?: emptyList()
                    )
                    _message.update { "Pakiet ćwiczeń zaktualizowany." }
                } else {
                    exercisePackageService.createExercisePackage(
                        exercisePackage,
                        processedUri?.let { listOf(it) } ?: emptyList()
                    )
                    _message.update { "Pakiet ćwiczeń został utworzony." }
                }
                navigate(WizardScreen.CreatorWizard.route)
            })
    }

    private suspend fun prepareExercisePackage(
        processedUri: Uri?,
        combinedExercises: List<String>
    ): ExercisePackage {
        val equipmentFromExercises =
            exerciseService.getEquipmentIdsForExercises(combinedExercises)
        return ExercisePackage(
            id = (if (_packageId.value.isNullOrEmpty()) null else _packageId.value).toString(),
            name = _packageName.value.orEmpty(),
            exerciseIds = _selectedExercises.value.toList(),
            warmUpIds = _selectedWarmUp.value.toList(),
            conditionIds = _selectedConditions.value.toList(),
            equipmentIds = equipmentFromExercises.values.flatten(),
            bodyPartIds = _selectedBodyParts.value.toList(),
            description = _packageDescription.value.orEmpty()
        )
    }

    private fun validateAndProcessMedia(
        uri: Uri,
        context: Context,
        onError: (String) -> Unit,
        onSuccess: (Uri) -> Unit
    ) {
        viewModelScope.launch {
            MediaProcessor.processMedia(
                context = context,
                uri = uri,
                onError = { errorMessage ->
                    _message.update { errorMessage }
                    onError(errorMessage)
                },
                onSuccess = { processedUri ->
                    addSelectedMedia(processedUri)
                    onSuccess(processedUri)
                }
            )
        }
    }

    companion object {
        private const val PACKAGE_VIEWMODEL_TAG = "PackageViewModel"
    }
}
