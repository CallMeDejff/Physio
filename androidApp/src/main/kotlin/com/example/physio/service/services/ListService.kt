package com.example.physio.service.services

interface ListService {
    suspend fun getEquipments(): List<Pair<String, String>>
    suspend fun getConditions(): List<Pair<String, String>>
    suspend fun getExercises(): List<Pair<String, String>>
    suspend fun getPackagesList(allPackages: Boolean): List<Pair<String, String>>
}