package com.example.physio.models

import com.google.gson.annotations.SerializedName

class Disease {
    @SerializedName("id_disease")
    var idDisease: Int = 0

    @SerializedName("name")
    var name: String? = null
}