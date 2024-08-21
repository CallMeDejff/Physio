package com.example.physio.models

import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("status")
    val status: String? = null

    @SerializedName("description")
    val description: String? = null

    @SerializedName("status_code")
    val statusCode: Int = 0

    @SerializedName("name")
    val name: String? = null

    @SerializedName("user_type")
    val userType: Int = 0

    @SerializedName("user_id")
    val userId: Int = 0
}
