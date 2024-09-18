package com.example.physio.models

import com.google.firebase.firestore.DocumentId

data class Exercise(
    @DocumentId val uid: String = "",
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val equipmentId: List<String> = emptyList(),
    val conditionId: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList()
)