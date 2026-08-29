package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: StoreViewModel, navController: NavController) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val ownerName by viewModel.ownerName.collectAsState()
    val storeName by viewModel.storeName.collectAsState()
    val bioEnabled by viewModel.biometricEnabled.collectAsState()
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val userEmail = context.getSharedPreferences("store_prefs", android.content.Context.MODE_PRIVATE).getString("user_email", "") ?: ""
    val isAdmin = userEmail.lowercase() == "abolfazlsh600@gmail.com"
    
    val textColor = if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val grayColor = if (isDarkMode) Color(0xFF9AA4B2) else Color(0xFF6B7280)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 90.dp)
    ) {
        // Topbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ChevronRight, contentDescription = "Back", tint = textColor)
            }
            Text("تنظیمات", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Support Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4FD1))))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp).background(Color.White.copy(alpha=0.18f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("پشتیبانی برنامه", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold)
                        Text("سوال، مشکل یا پیشنهاد داری؟ باهامون در تماس باش", color = Color.White.copy(alpha=0.85f), fontSize = 12.sp, modifier = Modifier.padding(top=2.dp))
                    }
                }
            }
        }

        // Help Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(listOf(Color(0xFF3B82F6), Color(0xFF1D4FD1))))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(46.dp).background(Color.White.copy(alpha=0.18f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Help, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("راهنمای استفاده از برنامه", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.ExtraBold)
                        Text("طرز کار هر بخش و نکات مهم برای درست‌کارکردن گزارش‌ها", color = Color.White.copy(alpha=0.85f), fontSize = 12.sp, modifier = Modifier.padding(top=2.dp))
                    }
                }
            }
        }

        Text("حساب کاربری", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(bottom = 10.dp))
        
        GlassCard(isDarkMode = isDarkMode) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(userEmail, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                    Text("ایمیل ورود · ${if(isAdmin) "مدیر سیستم" else "اشتراک فعال"}", fontSize = 12.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                }
                IconButton(onClick = { /* logout */ }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = textColor)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = { /* Change password */ },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
            border = androidx.compose.foundation.BorderStroke(1.dp, grayColor.copy(alpha = 0.3f))
        ) {
            Text("تغییر رمز عبور", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(14.dp))
        GlassCard(isDarkMode = isDarkMode) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("ورود با اثر انگشت", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                    Text("دفعه بعد با اثر انگشت گوشیت وارد شو", fontSize = 12.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                }
                Switch(checked = bioEnabled, onCheckedChange = { viewModel.setBiometric(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2563EB)))
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        GlassCard(isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(16.dp)) {
                var editStore by remember { mutableStateOf(storeName) }
                var editOwner by remember { mutableStateOf(ownerName) }
                
                Text("نام فروشگاه", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = editStore,
                    onValueChange = { editStore = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = grayColor.copy(alpha = 0.3f)
                    )
                )

                Text("نام فروشنده", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = editOwner,
                    onValueChange = { editOwner = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        unfocusedBorderColor = grayColor.copy(alpha = 0.3f)
                    )
                )

                Button(
                    onClick = { 
                        viewModel.setStoreName(editStore)
                        viewModel.setOwnerName(editOwner)
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("ذخیره تغییرات", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Text("ظاهر برنامه", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
        GlassCard(isDarkMode = isDarkMode) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("حالت شب (Dark Mode)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                    Text("چشم‌هات رو توی محیط تاریک اذیت نکن", fontSize = 12.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                }
                Switch(checked = isDarkMode, onCheckedChange = { viewModel.setDarkMode(it) }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF2563EB)))
            }
        }
        
        Text("پشتیبان‌گیری و انتقال داده", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
        GlassCard(isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier.fillMaxWidth().background(if(isDarkMode) Color.White.copy(alpha=0.1f) else Color(0xFFF3F4F6), RoundedCornerShape(12.dp)).padding(12.dp)
                ) {
                    Text("هنوز فایل پشتیبانی نگرفتی", color = grayColor, fontSize = 12.5.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { /* Backup */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Icon(Icons.Default.Backup, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("گرفتن فایل پشتیبان (Backup)", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = { /* Restore */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, grayColor.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.Restore, contentDescription = null, tint = textColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بازیابی از فایل پشتیبان", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("فایل پشتیبان شامل همه محصولات، فاکتورها، مشتریان، بدهی‌ها و تنظیماته. برای انتقال به گوشی جدید، همین فایل رو اونجا «بازیابی» کن.", fontSize = 11.5.sp, color = grayColor, lineHeight = 20.sp)
            }
        }
        
        Text("داده‌ها", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
        GlassCard(isDarkMode = isDarkMode) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedButton(
                    onClick = { /* Export PDF */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textColor),
                    border = androidx.compose.foundation.BorderStroke(1.dp, grayColor.copy(alpha = 0.3f))
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = textColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("دانلود گزارش کامل (PDF)", fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { /* Reset */ },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444).copy(alpha=0.14f), contentColor = Color(0xFFEF4444))
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("پاک کردن اطلاعات فروش (محصولات می‌مونن)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isAdmin) {
            Text("پنل مدیریت", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(top = 18.dp, bottom = 10.dp))
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, if(isDarkMode) Color(0xFF5a4a10) else Color(0xFFFDE68A)),
                colors = CardDefaults.cardColors(containerColor = if(isDarkMode) Color(0xFF2b2410) else Color(0xFFFFFBEB))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Box(modifier = Modifier.background(Color(0xFF2563EB), RoundedCornerShape(20.dp)).padding(horizontal=10.dp, vertical=4.dp)) {
                        Row(verticalAlignment=Alignment.CenterVertically) {
                            Icon(Icons.Default.Shield, contentDescription=null, tint=Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier=Modifier.width(4.dp))
                            Text("مدیر سیستم", color=Color.White, fontSize=11.5.sp, fontWeight=FontWeight.Bold)
                        }
                    }
                    Spacer(modifier=Modifier.height(12.dp))
                    Text("آستانه هشدار کم‌موجودی (عدد)", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = "5",
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                    Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
                        Text("ذخیره تنظیمات مدیریتی", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        
        Text("StoreFlow نسخه ۲.۰", color = grayColor, fontSize = 12.sp, modifier = Modifier.fillMaxWidth().padding(top = 20.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}
