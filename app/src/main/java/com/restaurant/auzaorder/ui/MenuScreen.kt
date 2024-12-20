package com.restaurant.auzaorder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.restaurant.auzaorder.R
import com.restaurant.auzaorder.models.MenuItem
import com.restaurant.auzaorder.models.MenuCategory
import com.restaurant.auzaorder.models.OrderItem
import com.restaurant.auzaorder.viewmodels.DashboardViewModel
import com.restaurant.auzaorder.viewmodels.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    orderViewModel: OrderViewModel = hiltViewModel()
) {
    val restaurant by dashboardViewModel.restaurantState.collectAsState()
    val menuCategories = remember { mutableStateOf<List<MenuCategory>>(emptyList()) }
    val order by orderViewModel.currentOrder.collectAsState()
    var itemQuantities by remember { mutableStateOf(mutableMapOf<String, Int>()) }

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchRestaurant("restaurant_1")
    }

    LaunchedEffect(restaurant) {
        menuCategories.value = restaurant?.menu?.categories ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = restaurant?.config?.name ?: "Menu") },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        )

        if (menuCategories.value.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Loading Menu...")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            ) {
                menuCategories.value.forEach { category ->
                    item(key = "header_${category.name}") {
                        CategoryHeader(category.name)
                    }

                    items(
                        items = category.items.values.toList(),
                        key = { menuItem -> menuItem.id }
                    ) { menuItem ->
                        MenuItemCard(
                            item = menuItem,
                            quantity = itemQuantities[menuItem.id] ?: 0,
                            onQuantityChange = { newQuantity ->
                                itemQuantities = itemQuantities.toMutableMap().apply {
                                    this[menuItem.id] = newQuantity
                                }
                            },
                            onAddToOrder = {
                                val currentQuantity = itemQuantities[menuItem.id] ?: 0
                                if (currentQuantity > 0) {
                                    orderViewModel.addItem(OrderItem(menuItem, currentQuantity))
                                    itemQuantities = itemQuantities.toMutableMap().apply {
                                        remove(menuItem.id)
                                    }
                                }
                            }
                        )
                    }

                    item(key = "spacer_${category.name}") {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        if (order.items.isNotEmpty()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        orderViewModel.placeOrder(restaurantId = "restaurant_1")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Place Order")
                }
            }
        }
    }
}

@Composable
private fun CategoryHeader(categoryName: String) {
    Text(
        text = categoryName,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun MenuItemCard(
    item: MenuItem,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    onAddToOrder: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.imageUrl)
                        .placeholder(R.drawable.ramen2)
                        .error(R.drawable.ramen2)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.name,
                    modifier = Modifier.size(80.dp)
                )

                Spacer(Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (item.description.isNotEmpty()) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "$${item.price}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onQuantityChange(maxOf(0, quantity - 1)) },
                    enabled = quantity > 0
                ) {
                    Icon(Icons.Filled.Remove, contentDescription = "Decrease")
                }

                Text(
                    text = "$quantity",
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                IconButton(
                    onClick = { onQuantityChange(quantity + 1) }
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Increase")
                }

                Spacer(Modifier.width(16.dp))

                Button(
                    onClick = onAddToOrder,
                    enabled = quantity > 0,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Add to Order")
                }
            }
        }
    }
}