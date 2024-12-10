package com.dawidkubica.physio.models

data class Exercise(
    val uid: String = "",
    val id: String = "",
    val title: String = "",
    val attempts: Int = 0,
    val time: Int = 0,
    val nonPublic: Boolean = false,
    val description: String = "",
    val equipmentIds: List<String> = emptyList(),
    val mediaUrls: List<String> = emptyList(),
    val mediaType: String = "",
)

