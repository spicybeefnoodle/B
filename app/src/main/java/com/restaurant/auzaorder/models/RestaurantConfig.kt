package com.restaurant.auzaorder.models

data class RestaurantConfig(
    val id: String = "", //Keep id in the config object
    val name: String = "",
    val tableNumbers: List<Int> = emptyList(),
    // Add other configuration properties as needed (logo URL, theme, etc.)
    val logoUrl: String? = null,
    val theme: String = "light" // Example theme setting
)