package com.dawidkubica.physio.models

data class Category(
    val title: String,
    val exercisePackages: List<ExercisePackage> = emptyList(),
    val isPremium: Boolean = false
)