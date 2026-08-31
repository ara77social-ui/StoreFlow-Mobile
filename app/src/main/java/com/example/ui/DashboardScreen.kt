package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.components.GlassCard
import com.example.ui.components.frostedGlass

@Composable
fun DashboardScreen(viewModel: StoreViewModel, navController: NavController) {
    val ownerName by viewModel.ownerName.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    val sales by viewModel.sales.collectAsState()
    val products by viewModel.products.collectAsState()

    val todayYMD = getTodayYMD()
    val yesterdayYMD = getYesterdayYMD()

    val todaySales = sales.filter { it.sale.date == todayYMD }
    val todayTotal = todaySales.sumOf { it.sale.finalTotal }
    
    val todayProfit = todaySales.sumOf { sale ->
        sale.items.sumOf { (it.price - (it.unitCostAtSale ?: 0.0)) * it.qty } - sale.sale.discount
    }

    val yesterdayTotal = sales.filter { it.sale.date == yesterdayYMD }.sumOf { it.sale.finalTotal }
    
    val lowStockProducts = products.filter { it.stock <= 5 }
    
    val textColor = if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val grayColor = if (isDarkMode) Color(0xFF9AA4B2) else Color(0xFF6B7280)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {
        // Topbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("سلام، $ownerName 👋", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                Text(getTodayFaDate(), fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
            }
            
            // Avatar
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2563EB))
                    .border(2.dp, if(isDarkMode) Color(0xFF0E1117) else Color.White, CircleShape)
                    .clickable { /* Avatar settings */ },
                contentAlignment = Alignment.Center
            ) {
                Text(ownerName.firstOrNull()?.toString() ?: "ک", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                
                // Camera Icon overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-3).dp, y = (-3).dp)
                        .size(17.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                        .border(2.dp, if(isDarkMode) Color(0xFF0E1117) else Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.White, modifier = Modifier.size(9.dp))
                }
            }
        }
        
        // 4 Stats Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardStat(
                    title = "فروش امروز",
                    value = formatCurrencyFa(todayTotal),
                    valueColor = Color(0xFF3B82F6),
                    icon = Icons.Default.Wallet,
                    iconColor = Color(0xFF3B82F6),
                    deltaText = if (yesterdayTotal > 0) {
                        val pct = (((todayTotal - yesterdayTotal) / yesterdayTotal) * 100).toInt()
                        if (pct >= 0) "+${formatNumberFa(pct)}٪ نسبت به دیروز" else "${formatNumberFa(pct)}٪ نسبت به دیروز"
                    } else if (todayTotal > 0.0) "اولین فروش نسبت به دیروز" else null,
                    isDeltaUp = yesterdayTotal == 0.0 || todayTotal >= yesterdayTotal,
                    isDarkMode = isDarkMode,
                    onClick = { /* Open Sales History */ }
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                DashboardStat(
                    title = "سود امروز",
                    value = formatCurrencyFa(todayProfit),
                    valueColor = Color(0xFF10B981),
                    icon = Icons.Default.TrendingUp,
                    iconColor = Color(0xFF10B981),
                    isDarkMode = isDarkMode
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                DashboardStat(
                    title = "تعداد کالا",
                    value = formatNumberFa(products.size),
                    valueColor = Color(0xFF3B82F6),
                    icon = Icons.Default.Inventory2,
                    iconColor = Color(0xFF3B82F6),
                    isDarkMode = isDarkMode
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                DashboardStat(
                    title = "کالای کم‌موجود",
                    value = formatNumberFa(lowStockProducts.size),
                    valueColor = Color(0xFFEF4444),
                    icon = Icons.Default.Warning,
                    iconColor = Color(0xFFEF4444),
                    isDarkMode = isDarkMode
                )
            }
        }

        Text(
            text = "برای دیدن تاریخچه فروش روزانه روی «فروش امروز» بزن",
            fontSize = 11.5.sp,
            color = grayColor,
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // New Sale Button
        Button(
            onClick = { navController.navigate("sale") { popUpTo("dashboard") { saveState = true }; launchSingleTop = true; restoreState = true } },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF3B82F6).copy(alpha=0.96f), Color(0xFF1D4FD1).copy(alpha=0.96f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("فروش جدید", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                }
            }
        }

        Text("دسترسی سریع", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))

        // Quick Access Grid
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                QuickAccessCard("محصولات", Icons.Default.Inventory2, { navController.navigate("products") { popUpTo("dashboard") { saveState = true }; launchSingleTop = true; restoreState = true } }, isDarkMode)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                QuickAccessCard("گزارش‌ها", Icons.Default.BarChart, { navController.navigate("reports") { popUpTo("dashboard") { saveState = true }; launchSingleTop = true; restoreState = true } }, isDarkMode)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                QuickAccessCard("مشتریان و حساب‌ها", Icons.Default.People, { navController.navigate("customers") { popUpTo("dashboard") { saveState = true }; launchSingleTop = true; restoreState = true } }, isDarkMode)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Box(modifier = Modifier.weight(1f)) {
                QuickAccessCard("ماشین‌حساب", Icons.Default.Calculate, { /* Open calculator */ }, isDarkMode)
            }
        }

        // Branch Switcher
        Spacer(modifier = Modifier.height(10.dp))
        GlassCard(isDarkMode = isDarkMode, onClick = { /* switch branch */ }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("شعبه دیگر", fontWeight = FontWeight.Bold, fontSize = 13.5.sp, color = textColor)
                        Text("شعبه اصلی", fontSize = 12.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                    }
                }
                Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = grayColor)
            }
        }

        Text("کالاهای کم‌موجود", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))

        if (lowStockProducts.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(30.dp).padding(bottom = 12.dp))
                Text("همه کالاها موجودی کافی دارن", color = grayColor, fontSize = 13.sp)
            }
        } else {
            lowStockProducts.forEach { p ->
                GlassCard(isDarkMode = isDarkMode, modifier = Modifier.padding(bottom = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(46.dp).background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(p.emoji ?: "📦", fontSize = 22.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(p.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Text("فقط ${formatNumberFa(p.stock)} عدد باقی مونده", fontSize = 12.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                            }
                        }
                        Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = grayColor)
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardStat(
    title: String,
    value: String,
    valueColor: Color,
    icon: ImageVector,
    iconColor: Color,
    isDarkMode: Boolean,
    deltaText: String? = null,
    isDeltaUp: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    val bgAlpha = if (isDarkMode) 0.15f else 0.6f
    val borderAlpha = if (isDarkMode) 0.08f else 0.55f
    val modifier = Modifier
        .fillMaxWidth()
        .frostedGlass(RoundedCornerShape(20.dp), backgroundAlpha = bgAlpha, borderAlpha = borderAlpha)
    
    val finalModifier = if (onClick != null) modifier.clickable { onClick() } else modifier

    Box(modifier = finalModifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(title, fontSize = 12.sp, color = if (isDarkMode) Color(0xFF9AA4B2) else Color(0xFF6B7280))
            }
            Text(
                text = value,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                color = valueColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (deltaText != null) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        if (isDeltaUp) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                        contentDescription = null,
                        tint = if (isDeltaUp) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = deltaText,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDeltaUp) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAccessCard(title: String, icon: ImageVector, onClick: () -> Unit, isDarkMode: Boolean) {
    GlassCard(isDarkMode = isDarkMode, onClick = onClick) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB), modifier = Modifier.size(24.dp).padding(bottom = 6.dp))
            Text(title, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937))
        }
    }
}
