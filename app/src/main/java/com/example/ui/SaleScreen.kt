package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.components.GlassCard
import com.example.ui.components.formatCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(viewModel: StoreViewModel, navController: NavController) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    
    val textColor = if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val grayColor = if (isDarkMode) Color(0xFF9AA4B2) else Color(0xFF6B7280)
    
    val categories = products.mapNotNull { it.category }.filter { it.isNotBlank() }.distinct()
    
    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true) &&
        (selectedCategory == null || it.category == selectedCategory)
    }

    val totalItems = cart.values.sum()
    val totalPrice = cart.entries.sumOf { (id, qty) ->
        val product = products.find { it.id == id }
        (product?.price ?: 0.0) * qty
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
                Text("ثبت فروش جدید", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("جستجوی محصول...", color = grayColor) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = grayColor) },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = if(isDarkMode) Color(0xFF374151) else Color(0xFFE5E7EB),
                        focusedContainerColor = if(isDarkMode) Color(0xFF1F2937).copy(alpha=0.5f) else Color.White.copy(alpha=0.5f),
                        unfocusedContainerColor = if(isDarkMode) Color(0xFF1F2937).copy(alpha=0.5f) else Color.White.copy(alpha=0.5f),
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    )
                )
                
                if (categories.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 4.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CategoryChip(
                            text = "همه",
                            selected = selectedCategory == null,
                            onClick = { selectedCategory = null },
                            isDarkMode = isDarkMode
                        )
                        categories.forEach { cat ->
                            CategoryChip(
                                text = cat,
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                isDarkMode = isDarkMode
                            )
                        }
                    }
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            AnimatedVisibility(visible = cart.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF3B82F6), Color(0xFF1D4FD1))
                            )
                        )
                        .clickable { navController.navigate("cart") }
                        .padding(horizontal = 20.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("$totalItems", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("سبد خرید", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                        Text(formatCurrency(totalPrice), color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        if (filteredProducts.isEmpty()) {
            Box(modifier = Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = grayColor.copy(alpha=0.3f), modifier = Modifier.size(64.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("محصولی یافت نشد", color = grayColor, fontSize = 16.sp)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(padding).fillMaxSize()
            ) {
                items(filteredProducts) { p ->
                    val inCart = cart[p.id] ?: 0
                    ProductCard(
                        product = p,
                        inCart = inCart,
                        isDarkMode = isDarkMode,
                        textColor = textColor,
                        grayColor = grayColor,
                        onAdd = { viewModel.addToCart(p.id) },
                        onRemove = { viewModel.updateCartQty(p.id, inCart - 1) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(text: String, selected: Boolean, onClick: () -> Unit, isDarkMode: Boolean) {
    val bg = if (selected) Color(0xFF2563EB) else if (isDarkMode) Color.White.copy(alpha=0.1f) else Color.White.copy(alpha=0.6f)
    val textColor = if (selected) Color.White else if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = textColor, fontSize = 13.sp, fontWeight = if(selected) FontWeight.Bold else FontWeight.Normal)
    }
}

@Composable
fun ProductCard(
    product: com.example.data.ProductEntity,
    inCart: Int,
    isDarkMode: Boolean,
    textColor: Color,
    grayColor: Color,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    GlassCard(isDarkMode = isDarkMode) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(Color(0xFFEEF2FF), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(product.emoji, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = product.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatCurrency(product.price),
                fontSize = 13.sp,
                color = Color(0xFF2563EB),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "موجودی: ${product.stock}",
                fontSize = 11.sp,
                color = grayColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            
            if (inCart > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if(isDarkMode) Color(0xFF374151) else Color(0xFFF3F4F6))
                            .clickable { onRemove() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = null, tint = textColor, modifier = Modifier.size(16.dp))
                    }
                    Text("$inCart", fontWeight = FontWeight.Bold, color = textColor)
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2563EB))
                            .clickable { if(product.stock > inCart) onAdd() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            } else {
                Button(
                    onClick = onAdd,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF), contentColor = Color(0xFF2563EB)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("افزودن", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
