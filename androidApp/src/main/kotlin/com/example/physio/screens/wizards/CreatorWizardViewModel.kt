package com.example.physio.screens.wizards

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.navigation.Graph
import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorWizardViewModel @Inject constructor(
    private val storageService: StorageService
) : PhysioAppViewModel() {

    private val _wizardMessage = MutableStateFlow<String?>(null)
    val wizardMessage = _wizardMessage.asStateFlow()

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _exerciseTitle = MutableStateFlow<String?>("")
    val exerciseTitle: StateFlow<String?> = _exerciseTitle

    private val _exerciseId = MutableStateFlow<String?>("")
    val exerciseId: StateFlow<String?> = _exerciseId

    private val _packageName = MutableStateFlow<String?>("")
    val packageName: StateFlow<String?> = _packageName

    private val _packageId = MutableStateFlow<String?>("")
    val packageId: StateFlow<String?> = _packageId

    private val _exerciseDescription = MutableStateFlow<String?>("")
    val exerciseDescription: StateFlow<String?> = _exerciseDescription

    private val _selectedMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUris: StateFlow<List<Uri>> = _selectedMediaUris

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment

    private val _selectedUsers = MutableStateFlow<Set<String>>(emptySet())
    val selectedUsers: StateFlow<Set<String>> = _selectedUsers

    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList

    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions

    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList

    private val _usersList = MutableStateFlow<List<User>>(emptyList())
    val usersList: StateFlow<List<User>> = _usersList

    private val _selectedExercises = MutableStateFlow<Set<String>>(emptySet())
    val selectedExercises: StateFlow<Set<String>> = _selectedExercises

    private val _selectedWarmUp = MutableStateFlow<Set<String>>(emptySet())
    val selectedWarmUp: StateFlow<Set<String>> = _selectedWarmUp

    private val _exercisesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exercisesList: StateFlow<List<Pair<String, String>>> = _exercisesList

    private val _packagesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val packagesList: StateFlow<List<Pair<String, String>>> = _packagesList

    private val _selectedPackages = MutableStateFlow<Set<String>>(emptySet())
    val selectedPackages: StateFlow<Set<String>> = _selectedPackages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadEquipmentList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _equipmentList.value = storageService.getEquipmentList()
            } catch (e: Exception) {
                _wizardMessage.value = "Ups! Nie udało się pobrać listy sprzętów"
            } finally {
                Log.d(CREATOR_WIZARD_TAG, "Equipment list loaded, item count: ${_equipmentList.value.size}")
                _isLoading.value = false
            }
        }
    }

    fun loadUsersList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val users = storageService.getUsersList()
                _usersList.value = users
            } catch (e: Exception) {
                _wizardMessage.value = "Ups! Nie udało się pobrać listy użytkowników"
            } finally {
                Log.d(CREATOR_WIZARD_TAG, "Users list loaded, item count: ${_usersList.value.size}")
                _isLoading.value = false
            }
        }
    }

    fun loadConditionList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _conditionsList.value = storageService.getConditionsList()
            } catch (e: Exception) {
                _wizardMessage.emit("Ups! Nie udało się pobrać listy schorzeń")
            } finally {
                Log.d(CREATOR_WIZARD_TAG, "Conditions list loaded, item count: ${_conditionsList.value.size}")
                _isLoading.value = false
            }
        }
    }

    fun loadExercisesList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _exercisesList.value = storageService.getExercises()
            } catch (e: Exception) {
                _wizardMessage.emit("Ups! Nie udało się pobrać listy schorzeń")
            } finally {
                Log.d(CREATOR_WIZARD_TAG, "Exercises list loaded, item count: ${_exercisesList.value.size}")
                _isLoading.value = false
            }
        }
    }

    fun loadPackagesList() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _packagesList.value = storageService.getPackagesList()
            } catch (e: Exception) {
                _wizardMessage.value = "Ups! Nie udało się pobrać listy sprzętów"
            } finally {
                Log.d(CREATOR_WIZARD_TAG, "Packages list loaded, item count: ${_packagesList.value.size}")
                _isLoading.value = false
            }
        }
    }

    private fun toggleItem(
        itemId: String,
        selectedItemsFlow: MutableStateFlow<Set<String>>,
        itemType: String,
        allowMultipleSelection: Boolean = true
    ) {
        selectedItemsFlow.update { selectedItems ->
            val newSet = if (allowMultipleSelection) {
                selectedItems.toMutableSet().apply {
                    if (contains(itemId)) {
                        remove(itemId)
                        Log.d(CREATOR_WIZARD_TAG, "$itemType removed: $itemId")
                    } else {
                        add(itemId)
                        Log.d(CREATOR_WIZARD_TAG, "$itemType added: $itemId")
                    }
                }
            } else {
                if (selectedItems.contains(itemId)) {
                    emptySet()
                } else {
                    setOf(itemId)
                }
            }
            Log.d(CREATOR_WIZARD_TAG, "Selected $itemType: ${newSet.toList()}")
            newSet
        }
    }


    fun toggleUser(userId: String) {
        toggleItem(userId, _selectedUsers, "Users")
    }


    fun toggleEquipment(equipmentId: String) {
        toggleItem(equipmentId, _selectedEquipment, "Equipment")
    }

    fun toggleCondition(conditionId: String, multipleSelection: Boolean) {
        if (multipleSelection) toggleItem(conditionId, _selectedConditions, "Condition")
        else toggleItem(conditionId, _selectedConditions, "Condition", false)
    }

    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) {
        if (multipleSelection) toggleItem(exerciseId, _selectedExercises, "Exercise")
        else toggleItem(exerciseId, _selectedExercises, "Exercise", false)
    }

    fun toggleWarmUp(exerciseId: String) {
        toggleItem(exerciseId, _selectedWarmUp, "Warm Up")
    }

    fun togglePackage(packageId: String) {
        toggleItem(packageId, _selectedPackages, "Package", false)
    }

    fun updateExerciseTitle(title: String) {
        _exerciseTitle.value = title
    }

    fun updatePackageName(packageName: String) {
        _packageName.value = packageName
    }

    fun updateExerciseDescription(description: String) {
        _exerciseDescription.value = description
    }

    fun addSelectedMedia(context: Context, uris: List<Uri>) {
        val validUris = uris.filter { uri ->
            if (isVideoUri(context, uri)) {
                val duration = getVideoDuration(context, uri)
                duration <= 60000
            } else {
                true
            }
        }

        _selectedMediaUris.update { currentList ->
            currentList.toMutableList().apply {
                addAll(validUris.filterNot { currentList.contains(it) })
            }
        }
    }

    fun removeMediaUri(uri: Uri) {
        _selectedMediaUris.update { currentList ->
            currentList.toMutableList().apply {
                remove(uri)
            }
        }
    }


    private fun isVideoUri(context: Context, uri: Uri): Boolean {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val hasVideo = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_VIDEO)
            hasVideo == "yes"
        } catch (e: Exception) {
            false
        } finally {
            retriever.release()
        }
    }

    private fun getVideoDuration(context: Context, uri: Uri): Long {
        val retriever = MediaMetadataRetriever()
        return try {
            retriever.setDataSource(context, uri)
            val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            Log.e(CREATOR_WIZARD_TAG, "Error retrieving video duration", e)
            0L
        } finally {
            retriever.release()
        }
    }

    fun getExerciseDetails() {
        launchCatching {
            _isLoading.value = true
            try {
                val selectedExerciseId = _selectedExercises.value.first()
                Log.d(CREATOR_WIZARD_TAG, "Selected exercise ID from getExerciseDetails: $selectedExerciseId")
                val exerciseDetails = storageService.getExercise(selectedExerciseId)
                exerciseDetails?.let { exercise ->
                    _exerciseId.value = exercise.id
                    _exerciseTitle.value = exercise.title
                    _exerciseDescription.value = exercise.description
                    _selectedEquipment.value = exercise.equipmentId.toSet()
                    _selectedMediaUris.value = exercise.mediaUrls.map { Uri.parse(it) }
                }

                Log.d(CREATOR_WIZARD_TAG, "Exercise details loaded: $exerciseDetails")
                Log.d(CREATOR_WIZARD_TAG, "Exercise details loaded: ${_exerciseTitle.value}, ${exerciseDescription.value}, ${selectedEquipment.value}, ${selectedConditions.value}, ${selectedMediaUris.value}")

            } catch (e: Exception) {
                _wizardMessage.emit("Nie udało się pobrać szczegółów ćwiczenia.")
                Log.e(CREATOR_WIZARD_TAG, "Error getting exercise details:", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPackageDetails() {
        launchCatching {
            _isLoading.value = true
            try {
                val selectedPackageId = _selectedPackages.value.first()
                Log.d(CREATOR_WIZARD_TAG, "Selected package ID from getPackageDetails: $selectedPackageId")
                val packageDetails = storageService.getPackage(selectedPackageId)
                packageDetails?.let { exercisePackage ->
                    _packageId.value = exercisePackage.id
                    _packageName.value = exercisePackage.name
                    _exerciseDescription.value = exercisePackage.description
                    _selectedEquipment.value = exercisePackage.equipmentIds.toSet()
                    _selectedConditions.value = exercisePackage.conditionIds.toSet()
                    _selectedExercises.value = exercisePackage.exerciseIds.toSet()
                    _selectedWarmUp.value = exercisePackage.warmUpIds.toSet()
                }

                Log.d(CREATOR_WIZARD_TAG, "Package details loaded: $packageDetails")
                Log.d(CREATOR_WIZARD_TAG, "Package details loaded: ${_packageName.value}, ${_exerciseDescription.value}, ${_selectedEquipment.value}, ${_selectedConditions.value}, ${_selectedExercises.value}, ${_selectedWarmUp.value}")

            } catch (e: Exception) {
                _wizardMessage.emit("Nie udało się pobrać szczegółów pakietu.")
                Log.e(CREATOR_WIZARD_TAG, "Error getting package details:", e)
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun onNewExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreateExerciseDetailsScreen.route)
    }

    fun onExerciseWizardContinueClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreateExerciseMediaScreen.route)
    }

    fun onEditExerciseClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditExerciseScreen.route)
    }

    fun onEditExerciseContinueClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditExerciseDetailsScreen.route)
    }

    fun onUpdateExerciseClick (navigate: (String) -> Unit) {
        val exercise = Exercise(
            id = _exerciseId.value.toString(),
            title = _exerciseTitle.value.toString(),
            equipmentId = _selectedEquipment.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            description = _exerciseDescription.value.toString()
        )
        Log.d(CREATOR_WIZARD_TAG, "Edited exercise data: $exercise")

        launchCatching {
            storageService.updateExercise(exercise, _selectedMediaUris.value)
        }
        navigate(WizardScreen.CreatorWizard.route)
    }


    fun onCreateExerciseClick(navigate: (String) -> Unit) {
        val exercise = Exercise(
            title = _exerciseTitle.value.toString(),
            equipmentId = _selectedEquipment.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            description = _exerciseDescription.value.toString()
        )
        Log.d(CREATOR_WIZARD_TAG, "Exercise data: $exercise")

        launchCatching {
            storageService.createExerciseWithMedia(exercise, _selectedMediaUris.value)
        }
        navigate(WizardScreen.CreatorWizard.route)
        _wizardMessage.update { "ćwiczenie utworzone" }
    }

    fun onNewPackageClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatePackage.route)
    }

    fun onCreatePackageClick(navigate: (String) -> Unit) {

        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching {
            val equipmentFromExercises =
                storageService.getEquipmentIdsForExercises(combinedExercises)

            val newPackage = ExercisePackage(
                name = _packageName.value.toString(),
                exerciseIds = _selectedExercises.value.toList(),
                description = _exerciseDescription.value.toString(),
                warmUpIds = _selectedWarmUp.value.toList(),
                equipmentIds = equipmentFromExercises.values.flatten(),
                conditionIds = _selectedConditions.value.toList()
            )
            Log.d(CREATOR_WIZARD_TAG, "Package data: $newPackage")
            storageService.createExercisePackage(newPackage)
        }
        navigate(WizardScreen.CreatorWizard.route)
        _wizardMessage.update { "Pakiet ćwiczeń utworzony" }
    }

    fun onEditPackageWizardClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditPackage.route)
    }

    fun onEditPackageClick(navigate: (String) -> Unit) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching {
            val equipmentFromExercises =
                storageService.getEquipmentIdsForExercises(combinedExercises)

            val editedPackage = ExercisePackage(
                id = _packageId.value.toString(),
                name = _packageName.value.toString(),
                exerciseIds = _selectedExercises.value.toList(),
                description = _exerciseDescription.value.toString(),
                warmUpIds = _selectedWarmUp.value.toList(),
                equipmentIds = equipmentFromExercises.values.flatten(),
                conditionIds = _selectedConditions.value.toList()
            )
            Log.d(CREATOR_WIZARD_TAG, "Edited package data: $editedPackage")
            storageService.updateExercisePackage(editedPackage)
        }
        navigate(WizardScreen.CreatorWizard.route)
        _wizardMessage.update { "Pakiet ćwiczeń zaktualizowany" }
    }

    fun onEditPackageContinueClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditPackageDetails.route)
    }


    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    fun onExitWizardClick(navigate: (String) -> Unit) {
        navigate(Graph.HOME)
    }

    fun clearLoginMessage() {
        _wizardMessage.update { null }
    }

    companion object {
        private const val CREATOR_WIZARD_TAG = "CreatorWizardViewModel"
    }

}