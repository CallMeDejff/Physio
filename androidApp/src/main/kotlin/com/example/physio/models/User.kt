package com.example.physio.models

data class User(
    val id: Int,
    val email: String,
    val newEmail: String,
    val firstName: String,
    val lastName: String,
    val password: String
)
