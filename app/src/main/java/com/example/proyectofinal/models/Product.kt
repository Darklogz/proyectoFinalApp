package com.example.proyectofinal.models

import com.google.firebase.firestore.DocumentId

data class Product(
    @DocumentId val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val category: String = "",
    val expiryDate: String? = null,
    val sku: String = "",
    val minStock: Int = 0,
    val imageUrl: String? = null
)