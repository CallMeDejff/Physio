package com.example.physio.models

import com.google.firebase.firestore.DocumentId

data class Condition(
    @DocumentId val id: String = "",
    val name: String = "",
    val description: String = ""
)