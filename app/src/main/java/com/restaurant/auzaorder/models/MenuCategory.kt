package com.restaurant.auzaorder.models

data class MenuCategory(
    val name: String = "",
    val items: List<MenuItem> = emptyList()
)