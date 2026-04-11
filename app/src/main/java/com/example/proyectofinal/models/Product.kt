package com.example.proyectofinal.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val category: String = "",
    val expiryDate: String? = null,
    val sku: String = "",
    val minStock: Int = 0,
    val imageUrl: String? = null
)