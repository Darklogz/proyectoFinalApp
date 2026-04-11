package com.example.proyectofinal.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sales")
data class Sale(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val total: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sale_items")
data class SaleItem(
    @PrimaryKey(autoGenerate = true) val itemId: Int = 0,
    val saleId: Int = 0,
    val productId: Int = 0,
    val productName: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0
)