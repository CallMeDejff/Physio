package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ExerciseShort(
    @field:SerializedName("title") val title: String, @field:SerializedName(
        "id_exercise"
    ) val idExercise: Int
)
