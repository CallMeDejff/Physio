package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ExerciseDetailedResponse {
    @SerializedName("description")
    val description: String? = null

    @SerializedName("title")
    val title: String? = null

    @SerializedName("id_exercise")
    val idExercise: Int = 0

    @SerializedName("diseases")
    val diseases: List<String>? = null

    @SerializedName("equipment")
    val equipment: List<String>? = null
}

