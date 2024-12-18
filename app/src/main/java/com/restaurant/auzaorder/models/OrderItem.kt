package com.restaurant.auzaorder.models

data class OrderItem(
    val menuItem: MenuItem = MenuItem(),
    val quantity: Int = 1,
    val modifiers: List<String> = emptyList()
)