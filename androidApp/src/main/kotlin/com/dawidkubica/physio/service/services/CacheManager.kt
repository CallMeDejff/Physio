package com.dawidkubica.physio.service.services

interface CacheManager {
    fun getCachedConditionsList(): List<Pair<String, String>>?
    fun setCachedConditionsList(conditionsList: List<Pair<String, String>>)
    fun getCachedEquipmentsList(): List<Pair<String, String>>?
    fun setCachedEquipmentsList(equipmentsList: List<Pair<String, String>>)
    fun setCachedBodyPartsList(bodyPartsList: List<Pair<String, String>>)
    fun getCachedBodyPartsList(): List<Pair<String, String>>?
}