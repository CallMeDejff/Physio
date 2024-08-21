package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ListDiseaseResponse {
    @SerializedName("diseases")
    val diseases: List<Disease>? = null
}