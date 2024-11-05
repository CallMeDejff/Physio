package com.example.physio.screens.wizards.viewmodels

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import com.example.physio.models.Exercise
import com.example.physio.navigation.WizardScreen
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.ExerciseService
import com.example.physio.service.services.ListService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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
    val exerciseAuthor: StateFlow<String?> = _exerciseAuthor

    private val _exercisesList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val exercisesList: StateFlow<List<Pair<String, String>>> = _exercisesList

    private val _selectedExercises = MutableStateFlow<Set<String>>(emptySet())
    val selectedExercises: StateFlow<Set<String>> = _selectedExercises

    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList

    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList

    fun loadExercises() {
        loadExercisesList(
            _exercisesList = _exercisesList,
            listService = listService,
            tag = EXERCISE_VIEWMODEL_TAG
        )
    }

    fun loadCondition() {
        loadConditionList(
            _conditionsList = _conditionsList,
            listService = listService,
            tag = EXERCISE_VIEWMODEL_TAG
        )
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

    fun addSelectedMedia(context: Context, uri: Uri) {
        if (isVideoUri(context, uri) && getVideoDuration(context, uri) > 60000) return
        _selectedMediaUris.update { listOf(uri) }
    }

    fun removeMediaUri(uri: Uri) {
        _selectedMediaUris.update { it.filter { existingUri -> existingUri != uri } }
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

        launchCatching(
            tag = EXERCISE_VIEWMODEL_TAG,
            errorMessage = "Nie udało się utworzyć ćwiczenia.",
            onError = { message -> _message.emit(message) },
            block = {
                exerciseService.createExerciseWithMedia(exercise, _selectedMediaUris.value)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "ćwiczenie utworzone" }
    }

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

        launchCatching(
            tag = EXERCISE_VIEWMODEL_TAG,
            errorMessage = "Nie udało się zaktualizować ćwiczenia.",
            onError = { message -> _message.emit(message) },
            block = {
                exerciseService.updateExercise(exercise, _selectedMediaUris.value)
            })
        navigate(WizardScreen.CreatorWizard.route)
        _message.update { "ćwiczenie zaktualizowane" }
    }

    fun deleteExercise(navigate: (String) -> Unit) {
        if (authenticateService.currentUserId == _exerciseAuthor.value.toString()) {
            launchCatching(
                tag = EXERCISE_VIEWMODEL_TAG,
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
            _message.update { "Nie jesteś autorem tego ćwiczenia" }
        }
    }

    fun getExerciseDetails() {
        launchCatching(
            tag = EXERCISE_VIEWMODEL_TAG,
            block = {
                _isLoading.value = true
                try {
                    val selectedExerciseId = _selectedExercises.value.first()
                    Log.d(
                        EXERCISE_VIEWMODEL_TAG,
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
                    Log.d(EXERCISE_VIEWMODEL_TAG, "Exercise details loaded: $exerciseDetails")
                } catch (e: Exception) {
                    _message.emit("Nie udało się pobrać szczegółów ćwiczenia.")
                    Log.e(EXERCISE_VIEWMODEL_TAG, "Error getting exercise details:", e)
                } finally {
                    _isLoading.value = false
                }
            })
    }

    private fun setMediaType(context: Context, uris: List<Uri>) {
        uris.forEach { uri ->
            val mimeType = context.contentResolver.getType(uri)
            when {
                mimeType?.startsWith("image/") == true -> _mediaType.value = "image"
                mimeType?.startsWith("video/") == true -> _mediaType.value = "video"
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
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
        } catch (e: Exception) {
            0L
        } finally {
            retriever.release()
        }
    }

    override fun updateDescription(description: String) {
        _exerciseDescription.value = description
    }

    fun toggleExercises(exerciseId: String, multipleSelection: Boolean) = toggleItem(
        exerciseId,
        _selectedExercises,
        allowMultipleSelection = multipleSelection,
        itemType = "Exercise"
    )

    fun toggleEquipment(equipmentId: String) =
        toggleItem(equipmentId, _selectedEquipment, itemType = "Equipment")

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
