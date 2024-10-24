package com.example.physio.service.impl

import com.example.physio.models.ExercisePackage
import com.example.physio.models.UserPackages
import com.example.physio.service.services.CacheManager
import javax.inject.Inject

class CacheManagerImpl @Inject constructor() : CacheManager {

    private var cachedExercisesList: List<Pair<String, String>>? = null
    private var cachedExercisePackages: List<ExercisePackage>? = null
    private var cachedConditionsList: List<Pair<String, String>>? = null
    private var cachedEquipmentsList: List<Pair<String, String>>? = null
    private var cachedUserPackages: UserPackages? = null

    private var cacheExpiryTime = 7 * 24 * 60 * 60 * 1000
    private var cachedExercisesListTimestamp: Long = 0
    private var cachedExercisePackagesTimestamp: Long = 0
    private var cachedConditionsListTimestamp: Long = 0
    private var cachedEquipmentsListTimestamp: Long = 0
    private var cachedFavoritesTimestamp: Long = 0

    override fun getCachedExercisesList(): List<Pair<String, String>>? {
        return if (isCacheExpired(cachedExercisesListTimestamp)) null else cachedExercisesList
    }

    override fun setCachedExercisesList(exercisesList: List<Pair<String, String>>) {
        cachedExercisesList = exercisesList
        cachedExercisesListTimestamp = System.currentTimeMillis()
    }

    override fun getCachedExercisePackages(): List<ExercisePackage>? {
        return if (isCacheExpired(cachedExercisePackagesTimestamp)) null else cachedExercisePackages
    }

    override fun setCachedExercisePackages(exercisePackages: List<ExercisePackage>) {
        cachedExercisePackages = exercisePackages
        cachedExercisePackagesTimestamp = System.currentTimeMillis()
    }

    override fun getCachedConditionsList(): List<Pair<String, String>>? {
        return if (isCacheExpired(cachedConditionsListTimestamp)) null else cachedConditionsList
    }

    override fun setCachedConditionsList(conditionsList: List<Pair<String, String>>) {
        cachedConditionsList = conditionsList
        cachedConditionsListTimestamp = System.currentTimeMillis()
    }

    override fun getCachedEquipmentsList(): List<Pair<String, String>>? {
        return if (isCacheExpired(cachedEquipmentsListTimestamp)) null else cachedEquipmentsList
    }

    override fun setCachedEquipmentsList(equipmentsList: List<Pair<String, String>>) {
        cachedEquipmentsList = equipmentsList
        cachedEquipmentsListTimestamp = System.currentTimeMillis()
    }

    override fun getCachedUserPackages(): UserPackages? {
        return if (isCacheExpired(cachedFavoritesTimestamp)) null else cachedUserPackages
    }

    override fun setCachedUserPackages(userPackages: UserPackages) {
        cachedUserPackages = userPackages
        cachedFavoritesTimestamp = System.currentTimeMillis()
    }

    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheExpiryTime
    }
}
