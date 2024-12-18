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

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val restaurantRepository: RestaurantRepository
): ViewModel() {
    var restaurantState = mutableStateOf<Restaurant?>(null)

    fun fetchRestaurant(restaurantId: String){
        restaurantRepository.getRestaurant(restaurantId).onEach { restaurant ->
            restaurantState.value = restaurant
        }.launchIn(viewModelScope)
    }

}