package com.restaurant.auzaorder.models

data class Restaurant(
    val config: RestaurantConfig? = null,
    val menu: Menu? = null,
    val tables: Map<String, Table>? = null
)