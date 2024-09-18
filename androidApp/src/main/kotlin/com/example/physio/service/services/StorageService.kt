package com.example.physio.service.services

import android.net.Uri
import com.example.physio.models.Exercise
import com.example.physio.models.ExercisePackage
import com.example.physio.models.User

interface StorageService {
    suspend fun createUser(user: User)
    suspend fun getUserInfo(userId: String): User?
    suspend fun createExercise(exercise: Exercise)
    suspend fun createExerciseWithMedia(exercise: Exercise, mediaUris: List<Uri>)
    suspend fun updateExercise(exercise: Exercise)
    suspend fun deleteExercise(exerciseId: String)
    suspend fun getExercise(exerciseId: String): Exercise?
    suspend fun getExercises(): List<Exercise>
    suspend fun uploadFilesToFirebase(uris: List<Uri>): List<String>
    suspend fun createExercisePackage(exercisePackage: ExercisePackage)
    suspend fun updateExercisePackage(exercisePackage: ExercisePackage)
    suspend fun deleteExercisePackage(exercisePackageId: String)
    suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage?
    suspend fun getExercisePackages(): List<ExercisePackage>
    suspend fun getEquipmentList(): List<Pair<String, String>>
    suspend fun getConditionsList(): List<Pair<String, String>>

    //suspend fun updateNote(user: User)
    //suspend fun deleteNote(noteId: String)
}
