package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ListFilteredExerciseResponse {
    @SerializedName("exercises")
    val exercises: List<ExerciseShort>? = null
}