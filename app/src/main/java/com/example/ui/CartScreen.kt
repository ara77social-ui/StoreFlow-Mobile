package com.example.ui

import com.example.ui.components.formatCurrency
import com.example.ui.components.StatCard

import androidx.compose.ui.unit.sp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(viewModel: StoreViewModel, navController: NavController) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    var discountText by remember { mutableStateOf("") }
    val discount = discountText.toDoubleOrNull() ?: 0.0

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("سبد خرید") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.clearCart() }) {
                        Icon(Icons.Default.Delete, contentDescription = "Clear Cart")
                    }
                }
            )
        }
    ) { padding ->
        if (cart.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("سبد خرید خالیه", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    val cartList = cart.toList()
                    items(cartList) { (productId, qty) ->
                        val p = products.find { it.id == productId }
                        if (p != null) {
                            GlassCard(isDarkMode = isDarkMode) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(p.name, fontWeight = FontWeight.Bold)
                                        Text(formatCurrency(p.price), color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        TextButton(onClick = { viewModel.updateCartQty(productId, qty - 1) }) { Text("-") }
                                        Text(qty.toString(), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp))
                                        TextButton(onClick = { viewModel.updateCartQty(productId, qty + 1) }) { Text("+") }
                                    }
                                }
                            }
                        }
                    }
                }
                
                var subtotal = 0.0
                cart.forEach { (productId, qty) ->
                    val p = products.find { it.id == productId }
                    if (p != null) subtotal += p.price * qty
                }
                val finalTotal = subtotal - discount

                GlassCard(isDarkMode = isDarkMode, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        OutlinedTextField(
                            value = discountText,
                            onValueChange = { discountText = it },
                            label = { Text("تخفیف (تومان)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("جمع کل کالاها", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(formatCurrency(subtotal))
                        }
                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("قابل پرداخت", fontWeight = FontWeight.Bold)
                            Text(formatCurrency(finalTotal), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                
                Button(
                    onClick = { 
                        viewModel.checkout(discount, null, false)
                        navController.navigate("dashboard")
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1), contentColor = Color.White)
                ) {
                    Text("تایید و ثبت فاکتور", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
