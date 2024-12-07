package com.dawidkubica.physio.screens.wizards.viewmodels

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.navigation.WizardScreen
import com.dawidkubica.physio.screens.wizards.services.MediaProcessor
import com.dawidkubica.physio.screens.wizards.services.Validator
import com.dawidkubica.physio.service.services.AccountService
import com.dawidkubica.physio.service.services.ExerciseService
import com.dawidkubica.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExerciseCreatorViewModel @Inject constructor(
    private val exerciseService: ExerciseService,
    private val listService: ListService,
    private val authenticateService: AccountService
) : SharedViewModel(), DescriptionUpdatable {

    private val _exerciseTitle = MutableStateFlow<String?>("")
    val exerciseTitle: StateFlow<String?> = _exerciseTitle

    private val _exerciseId = MutableStateFlow<String?>("")
    val exerciseId: StateFlow<String?> = _exerciseId

    private val _exerciseDescription = MutableStateFlow<String?>("")
    val exerciseDescription: StateFlow<String?> = _exerciseDescription

    private val _selectedMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUris: StateFlow<List<Uri>> = _selectedMediaUris

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment

    private val _mediaType = MutableStateFlow<String?>("")
    val mediaType: StateFlow<String?> = _mediaType

    private val _exerciseAuthor = MutableStateFlow<String?>("")
    private val _exercisesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exercisesList: StateFlow<List<Pair<String, String>>> = _exercisesList

    private val _selectedExercises = MutableStateFlow<Set<String>>(emptySet())
    val selectedExercises: StateFlow<Set<String>> = _selectedExercises

    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList

    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    private val _attempts = MutableStateFlow<Int?>(0)
    val attempts: StateFlow<Int?> = _attempts

    private val _time = MutableStateFlow<Int?>(0)
    val time: StateFlow<Int?> = _time

    private val _nonPublic = MutableStateFlow<Boolean?>(false)
    val nonPublic: StateFlow<Boolean?> = _nonPublic

    private val _isVideoProcessing = MutableStateFlow(false)
    val isVideoProcessing: StateFlow<Boolean> = _isVideoProcessing
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val titleError = MutableStateFlow<String?>(null)
    private val descriptionError = MutableStateFlow<String?>(null)
    private val equipmentError = MutableStateFlow<String?>(null)

    init {
        observeOnlyUserEntries()
    }

    private fun observeOnlyUserEntries() {
        viewModelScope.launch {
            _onlyUserEntries.collect { userEntriesOnly ->
                Log.d(EXERCISE_VIEWMODEL_TAG, "Only user entries changed: $userEntriesOnly")
                loadExercises(userEntriesOnly)
            }
        }
    }

    fun validateFields(
        title: String,
        description: String,
        selectedEquipment: List<String>,
        onValidationResult: (Boolean) -> Unit
    ) {
        launchCatching(
            tag = EXERCISE_VIEWMODEL_TAG,
            block = {
                val isValid = Validator.validateFields(
                    title = title,
                    description = description,
                    selectedEquipment = selectedEquipment,
                    titleError = titleError,
                    descriptionError = descriptionError,
                    equipmentError = equipmentError,
                    showMessage = { message -> _message.update { message } }
                )
                onValidationResult(isValid)
            }
        )
    }

    fun hasSelectedExercise(): Boolean = _selectedExercises.value.isNotEmpty()

    fun loadExercises(userEntriesOnly: Boolean) {
        loadExercisesList(
            _exercisesList = _exercisesList,
            listService = listService,
            tag = EXERCISE_VIEWMODEL_TAG,
            userEntriesOnly = userEntriesOnly
        )
    }

    fun loadCondition() {
        viewModelScope.launch {
            loadConditionList(
                listService = listService,
                _conditionsList = _conditionsList,
                tag = EXERCISE_VIEWMODEL_TAG
            )
        }
    }

    fun loadEquipmentList() {
        loadData(
            block = { listService.getEquipments() },
            onSuccess = { _equipmentList.value = it },
            errorMessage = "Ups! Nie udało się pobrać listy sprzętów."
        )
    }

    fun updateExerciseTitle(title: String) {
        _exerciseTitle.value = title
    }

    fun updateAttempts(attempts: Int) {
        _attempts.value = attempts
    }

    fun updateTime(time: Int) {
        _time.value = time
    }

    fun updateNonPublic(nonPublic: Boolean) {
        _nonPublic.value = nonPublic
    }

    fun addSelectedMedia(uri: Uri) {
        _selectedMediaUris.update { it + uri }
    }

    fun removeMediaUri(uri: Uri) {
        _selectedMediaUris.update { it.filter { it != uri } }
    }

    fun onCreateExerciseClick(context: Context, navigate: (String) -> Unit) {
        handleExerciseSave(context, navigate, isEdit = false)
    }

    fun onUpdateExerciseClick(context: Context, navigate: (String) -> Unit) {
        handleExerciseSave(context, navigate, isEdit = true)
    }

    fun getExerciseDetails() {
        viewModelScope.launch {
            try {
                val selectedExerciseId = _selectedExercises.value.firstOrNull()
                if (selectedExerciseId == null) {
                    _message.update { "Nie wybrano ćwiczenia." }
                    return@launch
                }

                val exerciseDetails = exerciseService.getExercise(selectedExerciseId)
                exerciseDetails?.let { exercise ->
                    _exerciseId.value = exercise.id
                    _exerciseTitle.value = exercise.title
                    _exerciseDescription.value = exercise.description
                    _exerciseAuthor.value = exercise.uid
                    _selectedEquipment.value = exercise.equipmentIds.toSet()
                    _selectedMediaUris.value = exercise.mediaUrls.map { Uri.parse(it.toString()) }
                    _mediaType.value = exercise.mediaType
                    _time.value = exercise.time
                    _attempts.value = exercise.attempts
                    _nonPublic.value = exercise.nonPublic
                } ?: run {
                    _message.update { "Nie udało się pobrać szczegółów ćwiczenia." }
                }
            } catch (e: Exception) {
                _message.update { "Wystąpił błąd podczas pobierania szczegółów ćwiczenia." }
            }
        }
    }

    private fun handleExerciseSave(
        context: Context,
        navigate: (String) -> Unit,
        isEdit: Boolean
    ) {
        _isUploading.update { true }
        setMediaType(context, _selectedMediaUris.value)
        val title = _exerciseTitle.value.orEmpty()
        val description = _exerciseDescription.value.orEmpty()
        val selectedEquipment = _selectedEquipment.value.toList()

        validateFields(
            title = title,
            description = description,
            selectedEquipment = selectedEquipment
        ) { isValid ->
            if (!isValid) {
                _isUploading.update { false }
                return@validateFields
            }

            val exercise = prepareExercise(isEdit)
            performExerciseAction(
                action = {
                    if (isEdit) exerciseService.updateExercise(exercise, _selectedMediaUris.value)
                    else exerciseService.createExerciseWithMedia(exercise, _selectedMediaUris.value)
                },
                successMessage = if (isEdit) "Ćwiczenie zaktualizowane." else "Ćwiczenie utworzone.",
                navigate = navigate
            )
        }
    }

    private fun prepareExercise(isEdit: Boolean): Exercise {
        return Exercise(
            id = (if (isEdit) _exerciseId.value else null).toString(),
            title = _exerciseTitle.value.orEmpty(),
            equipmentIds = _selectedEquipment.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            mediaType = _mediaType.value.orEmpty(),
            description = _exerciseDescription.value.orEmpty(),
            attempts = _attempts.value ?: 0,
            time = _time.value ?: 0,
            nonPublic = _nonPublic.value ?: false
        )
    }

    private fun performExerciseAction(
        action: suspend () -> Unit,
        successMessage: String,
        navigate: (String) -> Unit
    ) {
        viewModelScope.launch {
            runCatching { action() }
                .onSuccess {
                    _isUploading.update { false }
                    _message.update { successMessage }
                    navigate(WizardScreen.CreatorWizard.route)
                }
                .onFailure {
                    _isUploading.update { false }
                    _message.update { "Ups! Wystąpił błąd." }
                }
        }
    }

    fun deleteExercise(navigate: (String) -> Unit) {
        if (authenticateService.currentUserId == _exerciseAuthor.value.toString()) {
            performExerciseAction(
                action = {
                    exerciseService.deleteExercise(
                        Exercise(
                            id = _exerciseId.value.toString(),
                            mediaUrls = _selectedMediaUris.value.map { it.toString() }
                        )
                    )
                },
                successMessage = "Ćwiczenie zostało usunięte.",
                navigate = navigate
            )
        } else {
            _message.update { "Nie jesteś autorem tego ćwiczenia" }
        }
    }

    override fun updateDescription(description: String) {
        _exerciseDescription.value = description
    }

    private fun setMediaType(context: Context, uris: List<Uri>) {
        viewModelScope.launch {
            uris.forEach { uri ->
                val mimeType = context.contentResolver.getType(uri)
                if (mimeType?.startsWith("image/") == true) {
                    _mediaType.value = "image"
                } else if (mimeType?.startsWith("video/") == true) {
                    _mediaType.value = "video"
                    processVideo(context, uri)
                } else {
                    _mediaType.value = null
                }
            }
        }
    }

    private fun processVideo(context: Context, uri: Uri) {
        viewModelScope.launch {
            MediaProcessor.processMedia(
                context = context,
                uri = uri,
                setProcessing = { isProcessing ->
                    _isVideoProcessing.value = isProcessing
                },
                onError = { errorMessage ->
                    _message.update { "Błąd przetwarzania wideo: $errorMessage" }
                    _isVideoProcessing.update { false }
                },
                onSuccess = { processedUri ->
                    _selectedMediaUris.update { uris ->
                        uris.map { if (it == uri) processedUri else it }
                    }
                    _message.update { "Wideo zostało pomyślnie przetworzone." }
                    _isVideoProcessing.update { false }
                }
            )
        }
    }

    fun toggleEquipment(equipmentId: String) = toggleItem(
        itemId = equipmentId,
        selectedItemsFlow = _selectedEquipment,
        allowMultipleSelection = true,
        tag = EXERCISE_VIEWMODEL_TAG,
        itemType = "Equipment"
    )

    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) = toggleItem(
        itemId = exerciseId,
        selectedItemsFlow = _selectedExercises,
        allowMultipleSelection = multipleSelection,
        itemType = "Exercise",
        tag = EXERCISE_VIEWMODEL_TAG
    )

    fun onEditExerciseContinueClick(navigate: (String) -> Unit) {
        navigate(WizardScreen.EditExerciseDetailsScreen.route)
    }

    fun onGoBackClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    companion object {
        private const val EXERCISE_VIEWMODEL_TAG = "ExerciseViewModel"
    }
}
