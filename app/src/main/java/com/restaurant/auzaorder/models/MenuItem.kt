package com.restaurant.auzaorder.models

data class MenuItem(
    val id: String = "", //Provide default values
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String? = null,
    val category: String = "" // Add category if not already present
)
