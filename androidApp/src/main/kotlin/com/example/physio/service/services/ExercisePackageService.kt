package com.example.physio.service.services

import android.net.Uri
import com.example.physio.models.ExercisePackage
import com.example.physio.models.UserPackages

interface ExercisePackageService {
    suspend fun assignPackageToUser(userId: String, packageId: String)
    suspend fun getExercisePackages(): List<ExercisePackage>
    suspend fun createExercisePackage(exercisePackage: ExercisePackage, mediaUris: List<Uri>)
    suspend fun deleteExercisePackage(exercisePackage: ExercisePackage)
    suspend fun getPackage(packageId: String): ExercisePackage?
    suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage?
    suspend fun findMatchingExercisePackages(conditionIds: List<String>, equipmentIds: List<String>): List<ExercisePackage>
    suspend fun getUserExercisePackages(): UserPackages
    suspend fun removePackageFromUser(userId: String, packageId: String)
    suspend fun updateExercisePackage(exercisePackage: ExercisePackage, mediaUris: List<Uri>)
}