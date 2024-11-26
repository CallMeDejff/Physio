package com.dawidkubica.physio.service.services

import android.net.Uri
import com.dawidkubica.physio.models.Exercise
import com.dawidkubica.physio.models.ExercisePackage

interface StorageService {
    suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>)
    suspend fun createExercisePackage(exercisePackage: ExercisePackage)
    suspend fun deleteExercisePackage(exercisePackage: ExercisePackage)
    suspend fun deleteExercise(exercise: Exercise)
    suspend fun findMatchingExercisePackages(
        conditionIds: List<String>,
        equipmentIds: List<String>
    ): List<Triple<String, String, String>>

    suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage?
    suspend fun getExercisePackages(): List<ExercisePackage>
    suspend fun getEquipmentList(): List<Pair<String, String>>
    suspend fun getPackagesList(): List<Pair<String, String>>
    suspend fun getEquipmentIdsForExercises(exerciseIds: List<String>): Map<String, List<String>>
    suspend fun getConditionsList(): List<Pair<String, String>>
    suspend fun getExercise(exerciseId: String): Exercise?
    suspend fun getPackage(packageId: String): ExercisePackage?
    suspend fun getExercises(): List<Pair<String, String>>
    suspend fun updateExercise(exercise: Exercise, mediaUris: List<Uri>?)
    suspend fun updateExercisePackage(exercisePackage: ExercisePackage)
    suspend fun uploadFilesToFirebase(uris: List<Uri>, path: String): List<String>
}
