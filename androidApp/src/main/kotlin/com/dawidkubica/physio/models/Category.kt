package com.dawidkubica.physio.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val title: String,
    val icon: ImageVector?,
    val content: String,
    val exercisePackages: List<ExercisePackage> = emptyList(),
    val isPremium: Boolean = false
)