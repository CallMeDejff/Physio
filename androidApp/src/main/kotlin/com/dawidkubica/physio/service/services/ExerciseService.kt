package com.dawidkubica.physio.service.services

import android.net.Uri
import com.dawidkubica.physio.models.Exercise

interface ExerciseService {
    suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>)
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun getEquipmentIdsForExercises(exerciseIds: List<String>): Map<String, List<String>>
    suspend fun getExercise(exerciseId: String): Exercise?
    suspend fun updateExercise(exercise: Exercise, mediaUris: List<Uri>?)
}