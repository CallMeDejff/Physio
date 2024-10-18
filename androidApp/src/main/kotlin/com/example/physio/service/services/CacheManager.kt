package com.example.physio.service.services

import com.example.physio.models.ExercisePackage

interface CacheManager {
    fun getCachedExercisesList(): List<Pair<String, String>>?
    fun setCachedExercisesList(exercisesList: List<Pair<String, String>>)
    fun getCachedExercisePackages(): List<ExercisePackage>?
    fun setCachedExercisePackages(exercisePackages: List<ExercisePackage>)
    fun getCachedConditionsList(): List<Pair<String, String>>?
    fun setCachedConditionsList(conditionsList: List<Pair<String, String>>)
    fun getCachedEquipmentsList(): List<Pair<String, String>>?
    fun setCachedEquipmentsList(equipmentsList: List<Pair<String, String>>)
}