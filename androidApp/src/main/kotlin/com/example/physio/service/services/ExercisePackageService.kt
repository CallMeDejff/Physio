package com.example.physio.service.services

import com.example.physio.models.ExercisePackage

interface ExercisePackageService {
    suspend fun getExercisePackages(): List<ExercisePackage>
    suspend fun createExercisePackage(exercisePackage: ExercisePackage)
    suspend fun deleteExercisePackage(exercisePackage: ExercisePackage)
    suspend fun getPackage(packageId: String): ExercisePackage?
    suspend fun getExercisePackage(exercisePackageId: String): ExercisePackage?
    suspend fun findMatchingExercisePackages(
        conditionIds: List<String>,
        equipmentIds: List<String>
    ): List<Triple<String, String, String>>

    suspend fun updateExercisePackage(exercisePackage: ExercisePackage)
}