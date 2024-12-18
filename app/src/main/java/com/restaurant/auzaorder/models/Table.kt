package com.restaurant.auzaorder.models

data class Table(
    val status: String? = null,
    val orders: List<Order>? = null,
    val requests: List<String>? = null,
    val lastUpdate: Long? = null
)