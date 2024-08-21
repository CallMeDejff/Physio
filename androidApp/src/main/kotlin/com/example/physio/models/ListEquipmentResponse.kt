package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ListEquipmentResponse {
    @SerializedName("equipments")
    val equipments: List<Equipment>? = null
}
