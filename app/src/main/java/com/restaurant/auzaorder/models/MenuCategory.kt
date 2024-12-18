package com.restaurant.auzaorder.models

data class MenuCategory(
    val name: String? = null,
    val items: Map<String, MenuItem>? = null
)