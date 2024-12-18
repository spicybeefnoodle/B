package com.restaurant.auzaorder.models

data class Order(
    val items: List<OrderItem> = emptyList(),
    val tableId: Int = -1,
    val timestamp: Long = System.currentTimeMillis(),
    var status: String = ""
)
