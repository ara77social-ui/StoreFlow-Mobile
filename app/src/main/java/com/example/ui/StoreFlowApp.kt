package com.example.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.components.AppBackground
import com.example.ui.components.frostedGlass
import androidx.compose.ui.platform.LocalContext

@Composable
fun StoreFlowApp(viewModel: StoreViewModel) {
    val navController = rememberNavController()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val context = LocalContext.current
    var updateInfo by remember { mutableStateOf<UpdateManager.UpdateInfo?>(null) }

    LaunchedEffect(Unit) {
        updateInfo = UpdateManager.checkForUpdates()
    }
    
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AppBackground(isDarkMode = isDarkMode) {
            Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
                // Main content
                NavHost(
                    navController = navController,
                    startDestination = "dashboard",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 90.dp) // Space for the floating bottom nav
                ) {
                    composable("dashboard") { DashboardScreen(viewModel, navController) }
                    composable("sale") { SaleScreen(viewModel, navController) }
                    composable("cart") { CartScreen(viewModel, navController) }
                    composable("products") { ProductsScreen(viewModel, navController) }
                    composable("customers") { CustomersScreen(viewModel, navController) }
                    composable("reports") { ReportsScreen(viewModel, navController) }
                    composable("settings") { SettingsScreen(viewModel, navController) }
                }

                // Floating Liquid Glass Bottom Nav
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(0.9f)
                        .height(64.dp)
                        .frostedGlass(RoundedCornerShape(999.dp), backgroundAlpha = if(isDarkMode) 0.15f else 0.6f, borderAlpha = if(isDarkMode) 0.08f else 0.55f)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    NavItem("خانه", Icons.Default.Home, "dashboard", currentRoute, navController, isDarkMode)
                    NavItem("فروش", Icons.Default.ShoppingCart, "sale", currentRoute, navController, isDarkMode)
                    NavItem("گزارشات", Icons.Default.BarChart, "reports", currentRoute, navController, isDarkMode)
                    NavItem("محصولات", Icons.Default.Inventory2, "products", currentRoute, navController, isDarkMode)
                    NavItem("تنظیمات", Icons.Default.Settings, "settings", currentRoute, navController, isDarkMode)
                }

                if (updateInfo != null) {
                    AlertDialog(
                        onDismissRequest = { updateInfo = null },
                        title = { Text("بروزرسانی جدید") },
                        text = { Text("نسخه ${updateInfo!!.versionName} در دسترس است. آیا مایل به دانلود و نصب آن هستید؟") },
                        confirmButton = {
                            TextButton(onClick = {
                                UpdateManager.downloadAndInstall(context, updateInfo!!.downloadUrl)
                                updateInfo = null
                            }) {
                                Text("دانلود و نصب")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { updateInfo = null }) {
                                Text("بعداً")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RowScope.NavItem(
    label: String,
    icon: ImageVector,
    route: String,
    currentRoute: String?,
    navController: NavHostController,
    isDarkMode: Boolean
) {
    val selected = currentRoute == route || (route == "sale" && currentRoute == "cart")
    val interactionSource = remember { MutableInteractionSource() }
    
    val scale by animateFloatAsState(targetValue = if (selected) 1.08f else 1f, label = "scale")
    
    val selectedBgColor = if (isDarkMode) Color(0xFF60A5FA).copy(alpha = 0.15f) else Color(0xFF2563EB).copy(alpha = 0.14f)
    val selectedTextColor = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB)
    val unselectedTextColor = if (isDarkMode) Color(0xFFA0AAB0) else Color(0xFF4B5563)
    
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .padding(vertical = 6.dp, horizontal = 2.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) selectedBgColor else Color.Transparent)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo("dashboard") {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.scale(scale)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) selectedTextColor else unselectedTextColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                color = if (selected) selectedTextColor else unselectedTextColor,
                fontWeight = if (selected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
            )
        }
    }
}
