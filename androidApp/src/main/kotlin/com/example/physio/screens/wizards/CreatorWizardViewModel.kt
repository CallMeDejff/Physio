package com.example.physio.screens.wizards

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User
import com.example.physio.navigation.Graph
import com.example.physio.navigation.WizardScreen
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExercisePackageService
import com.example.physio.service.services.ExerciseService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CreatorWizardViewModel @Inject constructor(
    //private val storageService: StorageService,
    private val accountService: AccountService,
    private val exerciseService: ExerciseService,
    private val exercisePackageService: ExercisePackageService,
    private val listService: ListService
) : PhysioAppViewModel() {

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

    private val _packageAuthor = MutableStateFlow<String?>("")
    val packageAuthor: StateFlow<String?> = _packageAuthor

    private val _exerciseAuthor = MutableStateFlow<String?>("")
    val exerciseAuthor: StateFlow<String?> = _exerciseAuthor

    private val _mediaType = MutableStateFlow<String?>("")
    val mediaType: StateFlow<String?> = _mediaType

    private fun setMediaType(context: Context, uris: List<Uri>) {
        uris.forEach { uri ->
            val mimeType = context.contentResolver.getType(uri)
            Log.d(CREATOR_WIZARD_TAG, "URI: $uri, MIME Type: $mimeType")

            when {
                mimeType?.startsWith("image/") == true -> {
                    _mediaType.value = "image"
                    return
                }

                mimeType?.startsWith("video/") == true -> {
                    _mediaType.value = "video"
                    return
                }
            }
        }
    }

    fun loadEquipmentList() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy sprzętów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _equipmentList.value = listService.getEquipments()
                Log.d(
                    CREATOR_WIZARD_TAG,
                    "Equipment list loaded, item count: ${_equipmentList.value.size}"
                )
                _isLoading.value = false

            })
    }

    fun loadUsersList() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy użytkowników.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                val users = accountService.getUsersList()
                _usersList.value = users
                Log.d(CREATOR_WIZARD_TAG, "Users list loaded, item count: ${_usersList.value.size}")
                _isLoading.value = false

            })
    }

    fun loadConditionList() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy schorzeń.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _conditionsList.value = listService.getConditions()
                Log.d(
                    CREATOR_WIZARD_TAG,
                    "loadConditionList:Conditions list loaded, item count: ${_conditionsList.value.size}"
                )

                _isLoading.value = false

            })
    }

    fun loadExercisesList() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy ćwiczeń.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _exercisesList.value = listService.getExercises()
                Log.d(
                    CREATOR_WIZARD_TAG,
                    "Exercises list loaded, item count: ${_exercisesList.value.size}"
                )
                _isLoading.value = false

            })
    }

    fun loadPackagesList() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Ups! Nie udało się pobrać listy pakietów.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                _packagesList.value = listService.getPackagesList()
                Log.d(
                    CREATOR_WIZARD_TAG,
                    "Packages list loaded, item count: ${_packagesList.value.size}"
                )
                _isLoading.value = false

            })
    }

    fun toggleUser(userId: String) = toggleItem(userId, _selectedUsers, itemType = "User")
    fun toggleEquipment(equipmentId: String) = toggleItem(equipmentId, _selectedEquipment, itemType = "Equipment")
    fun toggleCondition(conditionId: String, multipleSelection: Boolean) = toggleItem(conditionId, _selectedConditions, allowMultipleSelection = multipleSelection, itemType = "Condition")
    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) = toggleItem(exerciseId, _selectedExercises, allowMultipleSelection = multipleSelection, itemType = "Exercise")
    fun toggleWarmUp(exerciseId: String) = toggleItem(exerciseId, _selectedWarmUp, itemType = "WarmUp")
    fun togglePackage(packageId: String) = toggleItem(packageId, _selectedPackages, allowMultipleSelection = false, itemType = "Package")

    fun updateExerciseTitle(title: String) { _exerciseTitle.value = title }

    fun updatePackageName(packageName: String) { _packageName.value = packageName }

    fun updateExerciseDescription(description: String) { _exerciseDescription.value = description }

    fun addSelectedMedia(context: Context, uri: Uri) {
        if (isVideoUri(context, uri)) {
            val duration = getVideoDuration(context, uri)
            if (duration > 60000) return
        }
        _selectedMediaUris.update { currentList ->
            currentList.toMutableList().apply {
                clear()
                add(uri)
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

    fun deletePackage(navigate: (String) -> Unit) {
        if (accountService.currentUserId == _packageAuthor.value.toString()) {
            launchCatching(
                tag = CREATOR_WIZARD_TAG,
                errorMessage = "Nie udało się usunąć pakietu ćwiczeń.",
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

    fun deleteExercise(navigate: (String) -> Unit) {
        if (accountService.currentUserId == _exerciseAuthor.value.toString()) {
            launchCatching(
                tag = CREATOR_WIZARD_TAG,
                errorMessage = "Nie udało się usunąć ćwiczenia.",
                onError = { message -> _message.emit(message) },
                block = {
                    val deletedExercise = Exercise(
                        id = _exerciseId.value.toString(),
                        mediaUrls = _selectedMediaUris.value.map { it.toString() }
                    )
                    exerciseService.deleteExercise(deletedExercise)
                }
            )
            navigate(WizardScreen.CreatorWizard.route)
        } else {
            _message.update { "Nie jesteś autorem tego pakietu" }
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
            val durationStr =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            durationStr?.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            Log.e(CREATOR_WIZARD_TAG, "Error retrieving video duration", e)
            0L
        } finally {
            retriever.release()
        }
    }

    fun getExerciseDetails() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            block = {
                _isLoading.value = true
                try {
                    val selectedExerciseId = _selectedExercises.value.first()
                    Log.d(
                        CREATOR_WIZARD_TAG,
                        "Selected exercise ID from getExerciseDetails: $selectedExerciseId"
                    )
                    val exerciseDetails = exerciseService.getExercise(selectedExerciseId)
                    exerciseDetails?.let { exercise ->
                        _exerciseId.value = exercise.id
                        _exerciseTitle.value = exercise.title
                        _exerciseAuthor.value = exercise.uid
                        _exerciseDescription.value = exercise.description
                        _selectedEquipment.value = exercise.equipmentIds.toSet()
                        _selectedMediaUris.value =
                            exercise.mediaUrls.map { Uri.parse(it.toString()) }
                        _mediaType.value = exercise.mediaType
                    }
                    Log.d(CREATOR_WIZARD_TAG, "Exercise details loaded: $exerciseDetails")
                } catch (e: Exception) {
                    _message.emit("Nie udało się pobrać szczegółów ćwiczenia.")
                    Log.e(CREATOR_WIZARD_TAG, "Error getting exercise details:", e)
                } finally {
                    _isLoading.value = false
                }
            })
    }

    fun getPackageDetails() {
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Nie udało się pobrać szczegółów pakietu.",
            onError = { message -> _message.emit(message) },
            block = {
                _isLoading.value = true
                val selectedPackageId = _selectedPackages.value.first()
                Log.d(
                    CREATOR_WIZARD_TAG,
                    "Selected package ID from getPackageDetails: $selectedPackageId"
                )
                val packageDetails = exercisePackageService.getPackage(selectedPackageId)
                packageDetails?.let { exercisePackage ->
                    _packageId.value = exercisePackage.id
                    _packageName.value = exercisePackage.name
                    _exerciseDescription.value = exercisePackage.description
                    _selectedEquipment.value = exercisePackage.equipmentIds.toSet()
                    _selectedConditions.value = exercisePackage.conditionIds.toSet()
                    _selectedExercises.value = exercisePackage.exerciseIds.toSet()
                    _selectedWarmUp.value = exercisePackage.warmUpIds.toSet()
                    _packageAuthor.value = exercisePackage.uid
                }
                Log.d(CREATOR_WIZARD_TAG, "Package details loaded: ${packageDetails}}")
                _isLoading.value = false
            })
    }

    fun onNewExerciseClick(navigate: (String) -> Unit) { navigate(WizardScreen.CreateExerciseDetailsScreen.route) }

    fun onEditExerciseClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditExerciseScreen.route) }

    fun onEditExerciseContinueClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditExerciseDetailsScreen.route) }

    fun onUpdateExerciseClick(context: Context, navigate: (String) -> Unit) {
        setMediaType(context = context, uris = _selectedMediaUris.value)
        val exercise = Exercise(
            id = _exerciseId.value.toString(),
            title = _exerciseTitle.value.toString(),
            equipmentIds = _selectedEquipment.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            mediaType = _mediaType.value.toString(),
            description = _exerciseDescription.value.toString()
        )
        Log.d(CREATOR_WIZARD_TAG, "Edited exercise data: $exercise")

        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Nie udało się zaktualizować ćwiczenia.",
            onError = { message -> _message.emit(message) },
            block = {
                exerciseService.updateExercise(exercise, _selectedMediaUris.value)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "ćwiczenie zaktualizowane" }
    }

    fun onCreateExerciseClick(context: Context, navigate: (String) -> Unit) {
        setMediaType(context = context, uris = _selectedMediaUris.value)
        val exercise = Exercise(
            title = _exerciseTitle.value.toString(),
            equipmentIds = _selectedEquipment.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            mediaType = _mediaType.value.toString(),
            description = _exerciseDescription.value.toString()
        )
        Log.d(CREATOR_WIZARD_TAG, "Exercise data: $exercise")
        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Nie udało się utworzyć ćwiczenia.",
            onError = { message -> _message.emit(message) },
            block = {
                exerciseService.createExerciseWithMedia(exercise, _selectedMediaUris.value)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "ćwiczenie utworzone" }
    }

    fun onNewPackageClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.CreatePackage.route)
    }

    fun onCreatePackageClick(navigate: (String) -> Unit) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Nie udało się utworzyć pakietu ćwiczeń.",
            onError = { message -> _message.emit(message) },
            block = {
                val equipmentFromExercises =
                    exerciseService.getEquipmentIdsForExercises(combinedExercises)

                val newPackage = ExercisePackage(
                    name = _packageName.value.toString(),
                    exerciseIds = _selectedExercises.value.toList(),
                    description = _exerciseDescription.value.toString(),
                    warmUpIds = _selectedWarmUp.value.toList(),
                    equipmentIds = equipmentFromExercises.values.flatten(),
                    conditionIds = _selectedConditions.value.toList()
                )
                Log.d(CREATOR_WIZARD_TAG, "Package data: $newPackage")
                exercisePackageService.createExercisePackage(newPackage)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "Pakiet ćwiczeń utworzony" }
    }

    fun onEditPackageWizardClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditPackage.route)
    }

    fun onEditPackageClick(navigate: (String) -> Unit) {
        val combinedExercises = _selectedExercises.value.toList() + _selectedWarmUp.value.toList()

        launchCatching(
            tag = CREATOR_WIZARD_TAG,
            errorMessage = "Nie udało się zaktualizować pakietu ćwiczeń.",
            onError = { message -> _message.emit(message) },
            block = {
                val equipmentFromExercises =
                    exerciseService.getEquipmentIdsForExercises(combinedExercises)

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
                exercisePackageService.updateExercisePackage(editedPackage)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "Pakiet ćwiczeń zaktualizowany" }
    }

    fun onEditPackageContinueClick(navigate: (String) -> Unit) { navigate(WizardScreen.EditPackageDetails.route) }

    fun onGoBackClick(popBackStack: () -> Unit) { popBackStack() }

    fun onExitWizardClick(navigate: (String) -> Unit) { navigate(Graph.HOME) }

    companion object {
        private const val CREATOR_WIZARD_TAG = "CreatorWizardViewModel"
    }
}