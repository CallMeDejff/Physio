package com.example.physio.models

import com.google.firebase.firestore.DocumentId

data class ExercisePackage(
    val id: String = "",
    val uid: String = "",
    val name: String = "",
    val description: String = "",
    val conditionIds: List<String> = emptyList(),
    val equipmentIds: List<String> = emptyList(),
    val warmUpIds: List<String> = emptyList(),
    val exerciseIds: List<String> = emptyList()
)