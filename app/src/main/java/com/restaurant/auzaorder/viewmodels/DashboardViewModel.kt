package com.restaurant.auzaorder.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.restaurant.auzaorder.models.Restaurant
import com.restaurant.auzaorder.repository.RestaurantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository,
    private val dataStore: DataStore<Preferences>,
): ViewModel() {
    var restaurantState = mutableStateOf<Restaurant?>(null)

    private val tableIdKey = stringPreferencesKey("table_id")

    fun fetchRestaurant(restaurantId: String){
        restaurantRepository.getRestaurant(restaurantId).onEach { restaurant ->
            restaurantState.value = restaurant
        }.launchIn(viewModelScope)
    }

    fun addTable(restaurantId: String, tableId: String){
        restaurantRepository.addTable(restaurantId, tableId)
    }

}