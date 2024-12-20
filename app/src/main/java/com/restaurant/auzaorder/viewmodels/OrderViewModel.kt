package com.restaurant.auzaorder.viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurant.auzaorder.models.Order
import com.restaurant.auzaorder.models.OrderItem
import com.restaurant.auzaorder.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    // Add this new state
    private val _itemQuantities = MutableStateFlow<Map<String, Int>>(emptyMap())
    val itemQuantities: StateFlow<Map<String, Int>> = _itemQuantities.asStateFlow()

    private val _currentOrder = MutableStateFlow<Order>(Order(tableId = -1)) // Initialize with an empty order
    val currentOrder: StateFlow<Order> = _currentOrder.asStateFlow()

    private var tableId: Int = -1

    init {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                tableId = preferences[stringPreferencesKey("table_id")]?.toIntOrNull() ?: -1
                _currentOrder.value = _currentOrder.value.copy(tableId = tableId)
            }
        }
    }

    fun addItem(orderItem: OrderItem) {
        val updatedItems = _currentOrder.value.items.toMutableList().also {
            val existingIndex = it.indexOfFirst { existingItem -> existingItem.menuItem.id == orderItem.menuItem.id }
            if (existingIndex >= 0) {
                // If item exists, update its quantity
                it[existingIndex] = it[existingIndex].copy(quantity = it[existingIndex].quantity + orderItem.quantity)
            } else {
                // Otherwise, add the new item
                it.add(orderItem)
            }
        }
        _currentOrder.value = _currentOrder.value.copy(items = updatedItems)
    }

    // Add these functions to manage quantities
    fun updateQuantity(itemId: String, quantity: Int) {
        _itemQuantities.value = _itemQuantities.value.toMutableMap().apply {
            this[itemId] = quantity
        }
    }

    fun getQuantity(itemId: String): Int {
        return _itemQuantities.value[itemId] ?: 0
    }

    fun placeOrder(restaurantId: String) {
        viewModelScope.launch {
            if (tableId != -1) {
                val order = _currentOrder.value.copy(
                    timestamp = System.currentTimeMillis(),
                    status = "Pending"
                )
                restaurantRepository.placeOrder(restaurantId, order)
                _currentOrder.value = Order(tableId = tableId) // Clear the order after placing
            } else {
                Log.e("OrderViewModel", "tableId is -1, cannot place order")
            }
        }
    }
}