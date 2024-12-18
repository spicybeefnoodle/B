package com.restaurant.auzaorder.models

data class OrderItem(
    val menuItem: MenuItem = MenuItem(), // Associate with MenuItem
    val quantity: Int = 1,
    val modifiers: List<String> = emptyList()
)
