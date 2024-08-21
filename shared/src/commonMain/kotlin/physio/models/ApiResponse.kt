package com.example.physio.models

import com.google.gson.annotations.SerializedName

class ApiResponse {
    @SerializedName("status")
    val status: String? = null

    @SerializedName("description")
    val description: String? = null

    @SerializedName("status_code")
    val statusCode: Int = 0
}
