package com.example.physio.screens.wizards

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.physio.models.Exercise
import com.example.physio.screens.PhysioAppViewModel
import com.example.physio.service.services.AccountService
import com.example.physio.service.services.StorageService
import com.example.physio.ui.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatorWizardViewModel @Inject constructor(
    private val accountService: AccountService,
    private val storageService: StorageService
) : PhysioAppViewModel() {

    init {
        Log.d(CREATOR_WIZARD_TAG, "CreatorWizardViewModel Created")
    }


    private val _wizardMessage = MutableStateFlow<String?>(null)
    val wizardMessage = _wizardMessage.asStateFlow()

    private val _showProgress = MutableStateFlow(false)
    val showProgress = _showProgress.asStateFlow()

    private val _exerciseTitle = MutableStateFlow<String?>("")
    val exerciseTitle: StateFlow<String?> = _exerciseTitle

    private val _exerciseDescription = MutableStateFlow<String?>("")
    val exerciseDescription: StateFlow<String?> = _exerciseDescription

    private val _selectedMediaUris = MutableStateFlow<List<Uri>>(emptyList())
    val selectedMediaUris: StateFlow<List<Uri>> = _selectedMediaUris

    private val _selectedEquipment = MutableStateFlow<Set<String>>(emptySet())
    val selectedEquipment: StateFlow<Set<String>> = _selectedEquipment

    private val _equipmentList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val equipmentList: StateFlow<List<Pair<String, String>>> = _equipmentList

    private val _selectedConditions = MutableStateFlow<Set<String>>(emptySet())
    val selectedConditions: StateFlow<Set<String>> = _selectedConditions

    private val _conditionsList = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val conditionsList: StateFlow<List<Pair<String, String>>> = _conditionsList

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
                _isLoading.value = false
            }
        }
    }

    fun toggleEquipment(equipmentId: String) {
        _selectedEquipment.update { selectedEquipment ->
            val newSet = selectedEquipment.toMutableSet().apply {
                if (contains(equipmentId)) {
                    remove(equipmentId)
                    Log.d(CREATOR_WIZARD_TAG, "Equipment removed: $equipmentId")
                } else {
                    add(equipmentId)
                    Log.d(CREATOR_WIZARD_TAG, "Equipment added: $equipmentId")
                }
            }
            newSet
        }
        Log.d(CREATOR_WIZARD_TAG, "Selected Equipment: ${_selectedEquipment.value.toList()}")

    }

    fun toggleDisease(diseaseId: String) {
        _selectedConditions.update { selectedConditions ->
            val newSet = selectedConditions.toMutableSet().apply {
                if (contains(diseaseId)) {
                    remove(diseaseId)
                    Log.d(CREATOR_WIZARD_TAG, "Disease removed: $diseaseId")
                } else {
                    add(diseaseId)
                    Log.d(CREATOR_WIZARD_TAG, "Disease added: $diseaseId")
                }
            }
            newSet
        }
        Log.d(CREATOR_WIZARD_TAG, "Selected Conditions: ${_selectedConditions.value.toList()}")
    }


    fun updateExerciseTitle(title: String) {
        _exerciseTitle.value = title
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

        _selectedMediaUris.value = validUris
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


    fun onNewPackageClick(navigate: (String) -> Unit) {
        //navigate(Screens.ExerciseEditor.route)
    }

    fun onNewExerciseClick(navigate: (String) -> Unit, viewModel: CreatorWizardViewModel) {
        navigate(Screens.ExerciseWizard.route)
    }

    fun onEditPackageClick(navigate: (String) -> Unit) {
        //navigate(Screens.ExerciseEditor.route)
    }

    fun onEditExerciseClick(navigate: (String) -> Unit, viewModel: CreatorWizardViewModel) {
        navigate(Screens.ExerciseWizard.route)
    }

    fun onExerciseWizardContinueClick(navigate: (String) -> Unit, viewModel: CreatorWizardViewModel) {
        navigate(Screens.ExerciseWizardEditor.route)
    }

    fun onCreateExerciseClick(navigate: (String) -> Unit, viewModel: CreatorWizardViewModel) {
        navigate(Screens.ExerciseWizard.route)

        val exercise = Exercise(
            title = _exerciseTitle.value.toString(),
            equipmentId = _selectedEquipment.value.toList(),
            conditionId = _selectedConditions.value.toList(),
            mediaUrls = _selectedMediaUris.value.map { it.toString() },
            description = _exerciseDescription.value.toString()
        )
        Log.d(CREATOR_WIZARD_TAG, "Exercise data: $exercise")
        Log.d(CREATOR_WIZARD_TAG, "Selected Conditions: ${_selectedConditions.value.toList()}")
        Log.d(CREATOR_WIZARD_TAG, "Selected Equipment: ${_selectedEquipment.value.toList()}")




        launchCatching {
        storageService.createExerciseWithMedia(exercise, _selectedMediaUris.value)
        }
    }

    fun onExitWizardClick(popBackStack: () -> Unit) {
        popBackStack()
    }

    fun clearLoginMessage() {
        _wizardMessage.update { null }
    }

    companion object {
        private const val CREATOR_WIZARD_TAG = "CreatorWizardViewModel"
    }

}