package com.example.proyectofinal.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Sale(
    @DocumentId val id: String = "",
    val items: List<SaleItem> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

data class SaleItem(
    val productId: String = "",
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)