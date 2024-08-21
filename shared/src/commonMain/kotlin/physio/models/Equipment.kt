package com.example.physio.models

import com.google.gson.annotations.SerializedName

class Equipment {
    @SerializedName("id_equipment")
    var idEquipment: Int = 0

    @SerializedName("name")
    var name: String? = null
}