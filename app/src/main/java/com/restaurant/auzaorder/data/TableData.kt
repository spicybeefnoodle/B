package com.restaurant.auzaorder.data

data class TableData(
    val status: String = "ordering",
    val orders: List<OrderItem> = emptyList(),
    val requests: List<String> = emptyList(),
    val lastUpdate: Long = System.currentTimeMillis()
)
