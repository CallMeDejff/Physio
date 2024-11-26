package com.dawidkubica.physio.models

data class ExercisePackage(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val premium: Boolean = true,
    val conditionIds: List<String> = emptyList(),
    val equipmentIds: List<String> = emptyList(),
    val bodyPartIds: List<String> = emptyList(),
    val description: String = "",
    val warmUpIds: List<String> = emptyList(),
    val exerciseIds: List<String> = emptyList(),
    val assignedTo: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList(),
)