package com.example.proyectofinal.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firebaseUid: String = "", // Link to Firebase Auth UID
    val name: String = "",
    val email: String = "",
    val role: String = "Vendedor",
    val active: Boolean = true
)