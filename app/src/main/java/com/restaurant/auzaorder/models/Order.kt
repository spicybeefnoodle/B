package com.restaurant.auzaorder.models

data class Order(
    val items: List<OrderItem> = emptyList(),
    val tableId: Int, // Use Int for tableId if that's how you identify tables
    val timestamp: Long = System.currentTimeMillis(),
    val status : String = ""
)