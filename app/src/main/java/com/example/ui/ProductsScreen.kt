package com.example.ui

import com.example.ui.components.formatCurrency
import com.example.ui.components.StatCard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.components.GlassCard
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(viewModel: StoreViewModel, navController: NavController) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
    val products by viewModel.products.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("محصولات") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, containerColor = Color(0xFF6366F1), contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        if (products.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("هنوز کالایی اضافه نکردی")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(products) { p ->
                    GlassCard(isDarkMode = isDarkMode, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(p.emoji, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(end = 16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(p.name, style = MaterialTheme.typography.titleMedium)
                                Text("${formatCurrency(p.price)} · موجودی: ${p.stock}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var priceText by remember { mutableStateOf("") }
        var costText by remember { mutableStateOf("") }
        var stockText by remember { mutableStateOf("") }
        var emoji by remember { mutableStateOf("📦") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("افزودن محصول جدید") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = emoji, onValueChange = { emoji = it }, label = { Text("ایموجی") }, singleLine = true)
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("نام محصول") }, singleLine = true)
                    OutlinedTextField(value = priceText, onValueChange = { priceText = it }, label = { Text("قیمت فروش (تومان)") }, singleLine = true)
                    OutlinedTextField(value = costText, onValueChange = { costText = it }, label = { Text("قیمت خرید (تومان) - اختیاری") }, singleLine = true)
                    OutlinedTextField(value = stockText, onValueChange = { stockText = it }, label = { Text("موجودی") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    val cost = costText.toDoubleOrNull()
                    val stock = stockText.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        viewModel.addProduct(com.example.data.ProductEntity(name = name, price = price, cost = cost ?: 0.0, stock = stock, emoji = emoji, category = null, packUnit = null, packSize = null, packPrice = null, createdAt = java.util.Date().toString()))
                        showAddDialog = false
                    }
                }) {
                    Text("ذخیره")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("انصراف")
                }
            }
        )
    }
}
