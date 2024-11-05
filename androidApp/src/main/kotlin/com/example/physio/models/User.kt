package com.example.physio.models

data class User(
    val uid: String = "",
    val name: String = "",
    val lastname: String = "",
    val email: String = "",
    val emailVerified: Boolean = false,
    val licenseNumber: Int = 0,
    val userType: Int = 0,
    val assignedPackages: List<String> = emptyList(),
    val favoritePackages: List<String> = emptyList(),
    val provider: String = "",
    val reminders: List<Reminder> = emptyList()
)