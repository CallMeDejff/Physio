package com.dawidkubica.physio.models

data class ExerciseMediaItem(
    val title: String,
    val mediaUrl: String,
    val mediaType: String,
    val time: Int?,
    val attempts: Int?
)
