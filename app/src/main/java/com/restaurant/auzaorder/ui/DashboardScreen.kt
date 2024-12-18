package com.restaurant.auzaorder.ui

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.viewmodel.compose.viewModel
import com.restaurant.auzaorder.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch

// DataStore declaration (extension property for Context)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(dashboardViewModel: DashboardViewModel = viewModel()) {
    val restaurantId = "restaurantID_1" // Replace with dynamic restaurantId later
    val tableIdState = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = context.dataStore // Access DataStore using the extension property
    val tableIdKey = stringPreferencesKey("table_id")

    // Read tableId from DataStore
    LaunchedEffect(Unit) {
        scope.launch {
            dataStore.data.collect { preferences ->
                tableIdState.value = preferences[tableIdKey] ?: ""
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard - $restaurantId")
        Text(text = "Current Table ID: ${tableIdState.value}")

        Spacer(modifier = Modifier.height(16.dp))

        var inputTableId by remember { mutableStateOf("") }
        OutlinedTextField(
            value = inputTableId,
            onValueChange = { inputTableId = it },
            label = { Text("Enter Table ID") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = {
                scope.launch {
                    dataStore.edit { preferences ->
                        preferences[tableIdKey] = inputTableId
                        tableIdState.value = inputTableId
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Table ID")
        }

        // Display restaurant tables
        dashboardViewModel.restaurantState.value?.let { restaurant ->
            if (restaurant.tables.isNullOrEmpty()) {
                Text("No tables available")
            } else {
                Text("Tables: ${restaurant.tables.keys.joinToString()}")
            }
        }
    }

    // Fetch restaurant details
    LaunchedEffect(Unit) {
        dashboardViewModel.fetchRestaurant(restaurantId)
    }
}
