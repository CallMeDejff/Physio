package com.dawidkubica.physio.service.services

import android.net.Uri
import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.UserPackages

interface ExercisePackageService {
    suspend fun assignPackageToUser(userId: String, packageId: String): Result<Unit>
    suspend fun getExercisePackages(): List<ExercisePackage>
    suspend fun createExercisePackage(exercisePackage: ExercisePackage, mediaUris: List<Uri>)
    suspend fun deleteExercisePackage(exercisePackage: ExercisePackage)
    suspend fun getPackage(packageId: String): ExercisePackage?
    suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage?
    suspend fun findMatchingExercisePackages(
        conditionIds: List<String>,
        equipmentIds: List<String>
    ): List<ExercisePackage>
    suspend fun getUserExercisePackages(): UserPackages
    suspend fun removePackageFromUser(userId: String, packageId: String)
    suspend fun updateExercisePackage(exercisePackage: ExercisePackage, mediaUris: List<Uri>)
}