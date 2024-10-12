package com.example.physio.models

data class ExerciseSummary(
    val uid: String = "",
    val id: String = "",
    val title: String = "",
    val equipmentId: List<String> = emptyList(),
    var details: List<ExerciseDetail> = emptyList()
)