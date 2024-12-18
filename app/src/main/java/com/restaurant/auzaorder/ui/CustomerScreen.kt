package com.restaurant.auzaorder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.restaurant.auzaorder.data.OrderItem
import com.google.firebase.database.FirebaseDatabase

@Composable
fun CustomerScreen(
    restaurantId: String = "restaurantID_1",
    tableId: String = "table_1"
) {
    // For testing: a button to place a dummy order
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Button(onClick = {
            val order = OrderItem(itemId = "pizzas/margherita", quantity = 2, modifiers = listOf("Extra Cheese"))
            val ref = FirebaseDatabase.getInstance().getReference("restaurants/$restaurantId/tables/$tableId/orders")
            // Get current orders, append a new one
            ref.get().addOnSuccessListener { snapshot ->
                val currentOrders = snapshot.children.mapNotNull { it.getValue(OrderItem::class.java) }
                val newOrders = currentOrders + order
                ref.setValue(newOrders).addOnCompleteListener {
                    if (it.isSuccessful) {
                        message = "Order placed!"
                    } else {
                        message = "Failed to place order: ${it.exception?.message}"
                    }
                }
            }
        }) {
            Text("Place Test Order")
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (message.isNotEmpty()) {
            Text(message)
        }
    }
}
