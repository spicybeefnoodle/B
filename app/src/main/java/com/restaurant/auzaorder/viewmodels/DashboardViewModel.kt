package com.restaurant.auzaorder.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurant.auzaorder.models.Restaurant
import com.restaurant.auzaorder.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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
    var restaurantState = mutableStateOf<Restaurant?>(null)
    private val tableIdKey = stringPreferencesKey("table_id")

    // Make fetchRestaurant a suspend function and launch a coroutine
    suspend fun fetchRestaurant(restaurantId: String) {
        try {
            val restaurant = restaurantRepository.getRestaurant(restaurantId)
            restaurantState.value = restaurant
        } catch (e: Exception) {
            // Handle exception (e.g., log the error, show an error message)
        }
    }

    // Alternatively, launch a coroutine within fetchRestaurant if you need to call it from a non-suspend context
    fun fetchRestaurantNonSuspend(restaurantId: String) {
        viewModelScope.launch {
            try {
                val restaurant = restaurantRepository.getRestaurant(restaurantId)
                restaurantState.value = restaurant
            } catch (e: Exception) {
                //Handle any exception
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

    // Add other ViewModel functions as needed (e.g., for placing orders, updating table status, etc.)
}
