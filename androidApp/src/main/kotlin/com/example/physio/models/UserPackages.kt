package com.example.physio.models

data class UserPackages(
    val favoritePackages: List<ExercisePackage?> = emptyList(),
    val assignedPackages: List<ExercisePackage?> = emptyList()
)