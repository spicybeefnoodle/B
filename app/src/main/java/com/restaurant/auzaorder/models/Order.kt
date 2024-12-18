package com.restaurant.auzaorder.models

data class Order(
    val itemId: String? = null,
    val quantity: Int? = null,
    val modifiers: List<String>? = null,
    val timestamp: Long? = null
)