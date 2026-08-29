package com.example.ui

import com.example.ui.components.formatCurrency
import com.example.ui.components.StatCard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.components.GlassCard
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: StoreViewModel, navController: NavController) {
    val isDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle(initialValue = false)
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    
    val totalSales = sales.sumOf { it.sale.finalTotal }
    val totalCount = sales.size
    val average = if (totalCount > 0) totalSales / totalCount else 0.0

    Scaffold(
        containerColor = Color.Transparent,
        topBar = { 
            TopAppBar(
                title = { Text("گزارش‌ها") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            ) 
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("کل فروش", formatCurrency(totalSales), Modifier.weight(1f), MaterialTheme.colorScheme.primary)
                    StatCard("تعداد فاکتور", totalCount.toString(), Modifier.weight(1f), MaterialTheme.colorScheme.onSurface)
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard("میانگین هر فاکتور", formatCurrency(average), Modifier.weight(1f), MaterialTheme.colorScheme.onSurface)
                }
            }

            item {
                Text("تاریخچه فاکتورها", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 16.dp))
            }

            if (sales.isEmpty()) {
                item {
                    Text("هنوز فروشی ثبت نشده", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                items(sales) { s ->
                    GlassCard(isDarkMode = isDarkMode, modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                val dateFa = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US).format(
                                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).parse(s.sale.datetime) ?: Date()
                                )
                                Text("فاکتور $dateFa", fontWeight = FontWeight.Bold)
                                Text("${s.items.size} قلم کالا", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Text(formatCurrency(s.sale.finalTotal), color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
