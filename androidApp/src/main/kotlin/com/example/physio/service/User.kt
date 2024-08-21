package com.example.physio.service

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val userId: String = "",
    val name: String = "",
    val lastname: String = "",
    val licenseNumber: Int = 0,
)