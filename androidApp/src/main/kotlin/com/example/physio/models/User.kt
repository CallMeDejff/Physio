package com.example.physio.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val uid: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val licenseNumber: Int = 0,
    val userType: Int = 0
)