package com.restaurant.auzaorder.models

data class MenuCategory(
    val name: String = "",
    val items: Map<String, MenuItem> = emptyMap()  // Changed from List to Map to match Firebase structure
)