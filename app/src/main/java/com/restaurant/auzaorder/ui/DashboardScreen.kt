package com.restaurant.auzaorder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.restaurant.auzaorder.data.TableData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun DashboardScreen(restaurantId: String = "restaurantID_1") {
    var tables by remember { mutableStateOf<Map<String, TableData>>(emptyMap()) }

    LaunchedEffect(restaurantId) {
        val ref = FirebaseDatabase.getInstance().getReference("restaurants/$restaurantId/tables")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val result = mutableMapOf<String, TableData>()
                for (tableSnapshot in snapshot.children) {
                    val tableId = tableSnapshot.key ?: continue
                    val data = tableSnapshot.getValue(TableData::class.java)
                    if (data != null) {
                        result[tableId] = data
                    }
                }
                tables = result
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Dashboard - $restaurantId", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            if (tables.isEmpty()) {
                item {
                    Text("No tables available", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                items(tables.entries.toList()) { (tableId, tableData) ->
                    Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("Table: $tableId", style = MaterialTheme.typography.titleMedium)
                            Text("Status: ${tableData.status}")
                            Text("Orders: ${tableData.orders.size}")
                            Text("Requests: ${tableData.requests.joinToString()}")
                        }
                    }
                }
            }
        }
    }
}
