package com.restaurant.auzaorder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurant.auzaorder.viewmodels.DashboardViewModel

@Composable
fun MenuScreen(dashboardViewModel: DashboardViewModel = hiltViewModel()) {
    val restaurant = dashboardViewModel.restaurantState.value
    Column {
        restaurant?.menu?.categories?.forEach { category -> // Directly iterate through the categories List
            Text("Category: ${category.name}") // Access the category name correctly
            category.items.forEach { item -> // Iterate through the items within each category
                Text("Item: ${item.name} - Price: ${item.price}") // Access item properties correctly
            }
        }
        if (restaurant == null) {
            Text("Loading menu...") // Display loading message while data is being fetched
        }

    }
}
