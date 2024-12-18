package com.restaurant.auzaorder.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.restaurant.auzaorder.models.Restaurant
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RestaurantRepository @Inject constructor() {
    private val database = FirebaseDatabase.getInstance()
    private val restaurantsRef = database.getReference("restaurants")

    fun getRestaurant(restaurantId: String): Flow<Restaurant?> = callbackFlow {
        val restaurantRef = restaurantsRef.child(restaurantId)

        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val restaurant = snapshot.getValue(Restaurant::class.java)
                trySend(restaurant)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        restaurantRef.addValueEventListener(listener)
        awaitClose { restaurantRef.removeEventListener(listener) }
    }

    fun addTable(restaurantId: String, tableId: String) {
        val tableRef = restaurantsRef.child(restaurantId).child("tables").child(tableId)
        tableRef.setValue(mapOf("status" to "waiting"))
    }

}