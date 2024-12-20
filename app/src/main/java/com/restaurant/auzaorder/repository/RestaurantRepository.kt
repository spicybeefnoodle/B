package com.restaurant.auzaorder.repository

import android.util.Log
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.restaurant.auzaorder.models.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RestaurantRepository @Inject constructor() {
    private val database: FirebaseDatabase = Firebase.database
    private val restaurantsRef = database.getReference("restaurants")

    // Use suspend function with await() for one-time fetch of Restaurant data
    suspend fun getRestaurant(restaurantId: String): Restaurant? {
        return try {
            val snapshot = restaurantsRef.child(restaurantId).get().await()

            // Log raw snapshot for debugging
            Log.d("RestaurantRepository", "Raw Snapshot Value: ${snapshot.value}")

            val restaurant = snapshot.getValue(Restaurant::class.java)

            if (restaurant == null) {
                Log.e(
                    "RestaurantRepository",
                    "Restaurant is null for ID: $restaurantId. Check database or data classes."
                )
                return null
            }

            // Additional detailed logs for debugging
            Log.d("RestaurantRepository", "Fetched Restaurant: $restaurant")
            Log.d("RestaurantRepository", "Menu: ${restaurant.menu}")
            restaurant.menu.categories.forEach { category ->
                Log.d("RestaurantRepository", "Category ${category.name}, Items: ${category.items}")
            }

            return restaurant
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error fetching restaurant: ${e.message}", e)
            null
        }
    }


        fun observeTableOrders(restaurantId: String, tableId: Int) =
            callbackFlow { // Use callbackFlow for real-time order updates
                val ordersRef =
                    restaurantsRef.child(restaurantId).child("tables").child(tableId.toString())
                        .child("orders") // Correct path
                val listener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val orders = snapshot.children.mapNotNull { it.getValue(Order::class.java) }
                        trySend(orders)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                }
                ordersRef.addValueEventListener(listener)
                awaitClose { ordersRef.removeEventListener(listener) }
            }

        //Keep your existing addTable function, it's already well-structured.
        fun addTable(restaurantId: String, tableId: String) {
            val tableRef = restaurantsRef.child(restaurantId).child("tables").child(tableId)
            tableRef.setValue(mapOf("status" to "waiting")) // Or Table("waiting") if you have a Table data class
        }

    // In RestaurantRepository.kt
    suspend fun placeOrder(restaurantId: String, order: Order) {
        try {
            val ordersRef = restaurantsRef.child(restaurantId).child("tables")
                .child(order.tableId.toString()).child("orders")
            ordersRef.push().setValue(order).await() //Use push for generating unique keys
            Log.d("RestaurantRepository", "Order placed successfully: $order")
        } catch (e: Exception) {
            Log.e("RestaurantRepository", "Error placing order: ${e.message}", e)
        }
    }

    // Add other necessary repository functions as needed (e.g., updating order status, etc.)
}
