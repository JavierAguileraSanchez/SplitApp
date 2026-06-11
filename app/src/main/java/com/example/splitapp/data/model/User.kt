package com.example.splitapp.data.model

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String = "",
    val nombre: String = "",
    val nombreLower: String = "",
    val email: String = "",
    val role: String = "user",
    val photoUrl: String = "",
    val telefono: String = ""
)
