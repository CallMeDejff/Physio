package com.example.physio.models


data class Exercise(
    val uid: String = "",
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val equipmentIds: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList(),
    val mediaType: String = "",
)