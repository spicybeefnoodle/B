package com.restaurant.auzaorder.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurant.auzaorder.models.Restaurant
import com.restaurant.auzaorder.models.MenuItem
import com.restaurant.auzaorder.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val dataStore: DataStore<Preferences>
) : ViewModel() {
    private val _restaurantState = MutableStateFlow<Restaurant?>(null)
    val restaurantState: StateFlow<Restaurant?> = _restaurantState.asStateFlow()

    private val _menuItems = MutableStateFlow<List<MenuItem>>(emptyList())
    val menuItems: StateFlow<List<MenuItem>> = _menuItems.asStateFlow()

    private val tableIdKey = stringPreferencesKey("table_id")

    suspend fun fetchRestaurant(restaurantId: String) {
        try {
            // ADDED: Debug logging
            Log.d("DashboardViewModel", "Fetching restaurant with ID: $restaurantId")
            val restaurant = restaurantRepository.getRestaurant(restaurantId)
            Log.d("DashboardViewModel", "Received restaurant: ${restaurant?.config?.name}")
            Log.d("DashboardViewModel", "Categories count: ${restaurant?.menu?.categories?.size}")
            _restaurantState.value = restaurant

            if (restaurant != null) {
                _menuItems.value = restaurant.menu.categories.flatMap { category ->
                    // ADDED: Debug logging
                    Log.d("DashboardViewModel", "Processing category: ${category.name} with ${category.items.size} items")
                    category.items.values
                }
            } else {
                // ADDED: Error logging
                Log.e("DashboardViewModel", "Restaurant is null")
            }
        } catch (e: Exception) {
            // ADDED: Error logging
            Log.e("DashboardViewModel", "Error fetching restaurant", e)
        }
    }

    fun fetchRestaurantNonSuspend(restaurantId: String) {
        viewModelScope.launch {
            try {
                val restaurant = restaurantRepository.getRestaurant(restaurantId)
                _restaurantState.value = restaurant

                // Update menuItems by flattening all items from all categories
                if (restaurant != null) {
                    _menuItems.value = restaurant.menu.categories.flatMap { category ->
                        category.items.values
                    }
                }
            } catch (e: Exception) {
                // Handle any exception
            }
        }
    }

    fun addTable(restaurantId: String, tableId: String) {
        viewModelScope.launch {
            restaurantRepository.addTable(restaurantId, tableId)
            dataStore.edit { preferences ->
                preferences[tableIdKey] = tableId
            }
        }
    }
}
