package com.restaurant.auzaorder.data

data class OrderItem(
    val itemId: String = "",
    val quantity: Int = 1,
    val modifiers: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
)