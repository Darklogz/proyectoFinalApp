package com.example.proyectofinal.models

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "Vendedor", // Admin, Vendedor, Almacén
    val active: Boolean = true
)