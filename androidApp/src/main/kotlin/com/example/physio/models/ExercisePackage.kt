package com.example.physio.models

data class ExercisePackage(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val conditionIds: List<String> = emptyList(),
    val equipmentIds: List<String> = emptyList(),
    val description: String = "",
    val warmUpIds: List<String> = emptyList(),
    val exerciseIds: List<String> = emptyList(),
    val assignedTo: List<String> = emptyList()
)