package com.restaurant.auzaorder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
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
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showCart by remember { mutableStateOf(false) }
    var showOrderHistory by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        dashboardViewModel.fetchRestaurant("restaurant_1")
    }

    LaunchedEffect(restaurant) {
        menuCategories.value = restaurant?.menu?.categories ?: emptyList()
        if (selectedCategory == null && menuCategories.value.isNotEmpty()) {
            selectedCategory = menuCategories.value.first().name
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = restaurant?.config?.name ?: "Menu") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Left Column - Categories
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 80.dp)
                ) {
                    items(menuCategories.value) { category ->
                        CategoryItem(
                            category = category,
                            isSelected = category.name == selectedCategory,
                            onClick = { selectedCategory = category.name }
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(8.dp)
                ) {
                    Button(
                        onClick = { /* TODO: Implement call staff functionality */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Call Staff")
                    }
                }
            }

            // Right Column - Menu Items
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                val currentCategory = menuCategories.value.find { it.name == selectedCategory }
                if (currentCategory != null) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 180.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp)    // For bottom bar
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)  // Single padding definition
                    ) {
                        val menuItems = currentCategory.items.values.toList()
                        items(menuItems.size) { index ->
                            val menuItem = menuItems[index]
                            var showDetails by remember { mutableStateOf(false) }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp),  // Reduced from 280.dp
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxHeight(),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Clickable area for details (image and text)
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { showDetails = true },
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center  // Center content vertically
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(menuItem.imageUrl)
                                                .placeholder(R.drawable.ramen2)
                                                .error(R.drawable.ramen2)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = menuItem.name,
                                            modifier = Modifier
                                                .size(120.dp)
                                                .padding(4.dp)
                                        )

                                        Text(
                                            text = menuItem.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            textAlign = TextAlign.Center,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        )

                                        Text(
                                            text = "${menuItem.price}",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }

                                    // Non-clickable area (Add to Cart button)
                                    Button(
                                        onClick = { orderViewModel.addItem(OrderItem(menuItem, 1)) },
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        Text("Add to Cart", style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                            }

                            if (showDetails) {
                                ItemDetailModal(
                                    menuItem = menuItem,
                                    onDismiss = { showDetails = false },
                                    onAddToCart = { orderViewModel.addItem(OrderItem(menuItem, 1)) }
                                )
                            }
                        }
                    }
                }

                // Bottom Navigation Bar
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(80.dp),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { showOrderHistory = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Order History")
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Button(
                            onClick = { showCart = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Cart")
                                if (order.items.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Surface(
                                        shape = MaterialTheme.shapes.extraLarge,
                                        color = MaterialTheme.colorScheme.primary
                                    ) {
                                        Text(
                                            text = order.items.sumOf { it.quantity }.toString(),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = MaterialTheme.colorScheme.onPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryItem(
    category: MenuCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isSelected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ItemDetailModal(
    menuItem: MenuItem,
    onDismiss: () -> Unit,
    onAddToCart: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(menuItem.imageUrl)
                        .placeholder(R.drawable.ramen2)
                        .error(R.drawable.ramen2)
                        .crossfade(true)
                        .build(),
                    contentDescription = menuItem.name,
                    modifier = Modifier
                        .size(200.dp)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = menuItem.name,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "${menuItem.price}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Text(
                    text = menuItem.description,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onAddToCart()
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Text("Add to Cart")
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}