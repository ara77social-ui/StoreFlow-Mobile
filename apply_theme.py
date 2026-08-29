import os

with open("app/src/main/java/com/example/ui/components/GlassComponents.kt", "w") as f:
    f.write("""package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

fun Modifier.frostedGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundAlpha: Float = 0.05f,
    borderAlpha: Float = 0.1f
): Modifier = this
    .background(Color.White.copy(alpha = backgroundAlpha), shape)
    .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
    .clip(shape)

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    
    val baseModifier = modifier
        .scale(scale)
        .frostedGlass(RoundedCornerShape(24.dp))
        
    val finalModifier = if (onClick != null) {
        baseModifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
    } else {
        baseModifier
    }
    
    Box(modifier = finalModifier, content = content)
}

@Composable
fun AppBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(Color(0xFF0F172A))
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF6366F1).copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(0f, 0f),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(0f, 0f),
                    radius = size.width * 0.8f
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFEC4899).copy(alpha = 0.2f), Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width * 0.8f
                    ),
                    center = Offset(size.width, size.height),
                    radius = size.width * 0.8f
                )
            }
    ) {
        content()
    }
}
""")

with open("app/src/main/java/com/example/ui/DashboardScreen.kt", "w") as f:
    f.write("""package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.ui.components.frostedGlass
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(viewModel: StoreViewModel, navController: NavController) {
    val ownerName by viewModel.ownerName.collectAsStateWithLifecycle()
    val sales by viewModel.sales.collectAsStateWithLifecycle()
    
    val todaySales = sales.filter { isToday(it.sale.date) }
    val todayTotal = todaySales.sumOf { it.sale.finalTotal }
    
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 48.dp, bottom = 24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF6366F1), Color(0xFFEC4899))),
                            shape = CircleShape
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val initial = if (ownerName.isNotBlank()) ownerName.take(2).uppercase() else "JD"
                    Text(initial, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("WELCOME BACK", fontSize = 10.sp, color = Color.White.copy(alpha = 0.5f), letterSpacing = 2.sp, fontWeight = FontWeight.Medium)
                    val name = if (ownerName.isNotBlank()) ownerName else "Julian Draxler"
                    Text(name, fontSize = 18.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .frostedGlass(CircleShape, backgroundAlpha = 0.1f, borderAlpha = 0.2f)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-10).dp, y = 10.dp)
                        .size(6.dp)
                        .background(Color(0xFFEC4899), CircleShape)
                        .border(1.dp, Color(0xFF0F172A), CircleShape)
                )
            }
        }

        // Stats Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .frostedGlass(RoundedCornerShape(32.dp))
                .padding(24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier
                        .background(Color(0xFF6366F1).copy(alpha = 0.2f), RoundedCornerShape(50))
                        .border(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f), RoundedCornerShape(50))
                        .padding(horizontal = 12.dp, vertical = 4.dp)) {
                        Text("REAL-TIME SYNC", color = Color(0xFFA5B4FC), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Text("Last sync: Just now", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                }
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                    Column {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(formatCurrencyCompact(todayTotal), fontSize = 36.sp, fontWeight = FontWeight.Light, color = Color.White)
                            Text(" T", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f), modifier = Modifier.padding(bottom = 6.dp, start = 4.dp))
                        }
                        Text("Today's sales revenue", fontSize = 12.sp, color = Color.White.copy(alpha = 0.6f))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
                        Box(modifier = Modifier.size(32.dp).background(Color(0xFF1E293B), CircleShape).border(2.dp, Color(0xFF312E81), CircleShape), contentAlignment = Alignment.Center) {
                            Text("DB", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.size(32.dp).background(Color(0xFF4F46E5), CircleShape).border(2.dp, Color(0xFF312E81), CircleShape), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Grid
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f).frostedGlass(RoundedCornerShape(24.dp)).padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFF10B981).copy(alpha=0.2f), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFF10B981).copy(alpha=0.3f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Fingerprint, contentDescription = null, tint = Color(0xFF34D399))
                    }
                    Column {
                        Text("Biometrics", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Enhanced security", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            }
            Box(modifier = Modifier.weight(1f).frostedGlass(RoundedCornerShape(24.dp)).padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.size(40.dp).background(Color(0xFFF97316).copy(alpha=0.2f), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFF97316).copy(alpha=0.3f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFFFB923C))
                    }
                    Column {
                        Text("Offline Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Available locally", fontSize = 10.sp, color = Color.White.copy(alpha = 0.4f))
                    }
                }
            }
        }

        // Vault Button
        Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Box(modifier = Modifier.size(120.dp).background(Brush.radialGradient(listOf(Color(0xFF6366F1).copy(alpha=0.5f), Color.Transparent)), CircleShape).blur(32.dp))
                    Box(
                        modifier = Modifier.size(96.dp).frostedGlass(CircleShape, backgroundAlpha = 0.05f, borderAlpha = 0.2f).clickable { navController.navigate("sale") },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LockOpen, contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(36.dp))
                    }
                }
                Text("Tap button to authenticate\\nand access the sales vault.", fontSize = 14.sp, color = Color.White.copy(alpha = 0.7f), textAlign = TextAlign.Center)
            }
        }
    }
}

fun formatCurrencyCompact(amount: Double): String {
    if (amount == 0.0) return "0"
    if (amount >= 1_000_000) return String.format(Locale.US, "%.1fM", amount / 1_000_000)
    if (amount >= 1_000) return String.format(Locale.US, "%.1fK", amount / 1_000)
    return String.format(Locale.US, "%.0f", amount)
}

fun isToday(dateStr: String): Boolean {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    return dateStr == df.format(Date())
}
""")

with open("app/src/main/java/com/example/ui/StoreFlowApp.kt", "w") as f:
    f.write("""package com.example.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import com.example.ui.components.AppBackground

@Composable
fun StoreFlowApp(viewModel: StoreViewModel) {
    val navController = rememberNavController()

    AppBackground {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                NavigationBar(
                    containerColor = Color.White.copy(alpha = 0.05f),
                    contentColor = Color.White,
                    modifier = Modifier.height(80.dp).drawBehind {
                        drawLine(Color.White.copy(alpha = 0.1f), Offset(0f, 0f), Offset(size.width, 0f), strokeWidth = 1f)
                    }
                ) {
                    val colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFFA5B4FC),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFF6366F1).copy(alpha = 0.2f),
                        unselectedIconColor = Color.White.copy(alpha = 0.4f),
                        unselectedTextColor = Color.White.copy(alpha = 0.4f)
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home", fontSize = 10.sp) },
                        selected = currentRoute == "dashboard",
                        onClick = { navController.navigate("dashboard") },
                        colors = colors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Storage, contentDescription = "Data") },
                        label = { Text("Data", fontSize = 10.sp) },
                        selected = currentRoute == "products" || currentRoute == "reports",
                        onClick = { navController.navigate("products") },
                        colors = colors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Security, contentDescription = "Vault") },
                        label = { Text("Vault", fontSize = 10.sp) },
                        selected = currentRoute == "sale" || currentRoute == "cart",
                        onClick = { navController.navigate("sale") },
                        colors = colors
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                        label = { Text("Config", fontSize = 10.sp) },
                        selected = currentRoute == "settings",
                        onClick = { navController.navigate("settings") },
                        colors = colors
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "dashboard",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("dashboard") { DashboardScreen(viewModel, navController) }
                composable("sale") { SaleScreen(viewModel, navController) }
                composable("cart") { CartScreen(viewModel, navController) }
                composable("products") { ProductsScreen(viewModel, navController) }
                composable("reports") { ReportsScreen(viewModel, navController) }
                composable("settings") { SettingsScreen(viewModel, navController) }
            }
        }
    }
}
""")
