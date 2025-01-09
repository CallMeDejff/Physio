package com.dawidkubica.physio.service.impl

import com.dawidkubica.physio.service.services.CacheManager
import javax.inject.Inject

class CacheManagerImpl @Inject constructor() : CacheManager {

    private var cachedConditionsList: List<Pair<String, String>>? = null
    private var cachedEquipmentsList: List<Pair<String, String>>? = null
    private var cachedBodyPartsList: List<Pair<String, String>>? = null

    private var cacheExpiryTime = 60 * 60
    private var cachedConditionsListTimestamp: Long = 0
    private var cachedEquipmentsListTimestamp: Long = 0
    private var cachedBodyPartsListTimestamp: Long = 0

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

    override fun setCachedBodyPartsList(bodyPartsList: List<Pair<String, String>>) {
        cachedBodyPartsList = bodyPartsList
        cachedBodyPartsListTimestamp = System.currentTimeMillis()
    }

    override fun getCachedBodyPartsList(): List<Pair<String, String>>? {
        return if (isCacheExpired(cachedBodyPartsListTimestamp)) null else cachedBodyPartsList
    }

    override fun setCachedEquipmentsList(equipmentsList: List<Pair<String, String>>) {
        cachedEquipmentsList = equipmentsList
        cachedEquipmentsListTimestamp = System.currentTimeMillis()
    }

    private fun isCacheExpired(timestamp: Long): Boolean {
        return System.currentTimeMillis() - timestamp > cacheExpiryTime
    }
}
