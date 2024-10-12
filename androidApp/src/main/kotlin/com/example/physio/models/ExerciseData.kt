package com.example.physio.models

data class ExerciseData(
    val uid: String = "",
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val equipmentId: List<String> = emptyList(),
    val conditionId: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList()
)