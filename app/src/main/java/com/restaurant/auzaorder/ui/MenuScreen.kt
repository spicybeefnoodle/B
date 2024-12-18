package com.restaurant.auzaorder.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.restaurant.auzaorder.viewmodels.DashboardViewModel


@Composable
fun MenuScreen(dashboardViewModel: DashboardViewModel = hiltViewModel()) {
    Column {
        dashboardViewModel.restaurantState.value?.menu?.categories?.let { categories ->
            Text("Menu Screen")
            categories.forEach { (key, category) ->
                Text("Category: ${category.name}")
                category.items?.forEach { (itemKey, item) ->
                    Text("Item: ${item.name} - Price: ${item.price}")
                }
            }

        }

    }
}