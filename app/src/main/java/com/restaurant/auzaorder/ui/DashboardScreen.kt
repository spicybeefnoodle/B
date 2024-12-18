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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController // Import NavHostController
import com.restaurant.auzaorder.viewmodels.DashboardViewModel
import kotlinx.coroutines.launch
import kotlin.properties.ReadOnlyProperty
import androidx.compose.runtime.getValue

// DataStore declaration (extension property for Context)
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController, // Add NavHostController parameter
    dashboardViewModel: DashboardViewModel = hiltViewModel()
) {
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
        dashboardViewModel.restaurantState.value?.config?.name?.let {
            Text(text = "Dashboard - $it")
        }?:  Text(text = "Dashboard - Loading...") //Added null check for text and loading state

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
                    dashboardViewModel.restaurantState.value?.let { restaurant ->
                        dashboardViewModel.addTable(restaurantId,inputTableId)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Table ID")
        }

        // Display restaurant tables
        if (dashboardViewModel.restaurantState.value?.tables.isNullOrEmpty()) {
            Text("No tables available")
        } else{
            dashboardViewModel.restaurantState.value?.tables?.let {
                Text("Tables: ${it.keys.joinToString()}")
            }

        }
        // Added the Button to navigate to the menu
        Button(
            onClick = {
                navController.navigate("menu") // Navigate to the menu screen
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("View Menu")
        }
    }

    // Fetch restaurant details
    LaunchedEffect(Unit) {
        dashboardViewModel.fetchRestaurant(restaurantId)
    }
}