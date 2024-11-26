package com.dawidkubica.physio.service.services

import com.dawidkubica.physio.models.ExercisePackage
import com.dawidkubica.physio.models.UserPackages

interface CacheManager {
    fun getCachedExercisesList(): List<Pair<String, String>>?
    fun setCachedExercisesList(exercisesList: List<Pair<String, String>>)
    fun getCachedExercisePackages(): List<ExercisePackage>?
    fun setCachedExercisePackages(exercisePackages: List<ExercisePackage>)
    fun getCachedConditionsList(): List<Pair<String, String>>?
    fun setCachedConditionsList(conditionsList: List<Pair<String, String>>)
    fun getCachedEquipmentsList(): List<Pair<String, String>>?
    fun setCachedEquipmentsList(equipmentsList: List<Pair<String, String>>)
    fun setCachedBodyPartsList(bodyPartsList: List<Pair<String, String>>)
    fun getCachedBodyPartsList(): List<Pair<String, String>>?
    fun getCachedUserPackages(): UserPackages?
    fun setCachedUserPackages(userPackages: UserPackages)
}