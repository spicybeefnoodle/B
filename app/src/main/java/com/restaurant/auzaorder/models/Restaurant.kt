package com.restaurant.auzaorder.models

data class Restaurant(
    val config: RestaurantConfig = RestaurantConfig(),
    val menu: Menu = Menu(),
    val tables: Map<String, Table> = emptyMap() // Use Map<String, Table> for easy lookup by tableId
)
