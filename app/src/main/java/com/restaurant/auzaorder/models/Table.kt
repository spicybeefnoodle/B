package com.restaurant.auzaorder.models

data class Table(
    val id: Int = -1,
    var status: String = "Available" // "Available", "Ordering", "Waiting", etc.
)
