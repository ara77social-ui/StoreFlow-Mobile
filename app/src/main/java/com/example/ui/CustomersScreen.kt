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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.data.CustomerEntity
import com.example.data.LedgerEntity
import com.example.ui.components.GlassCard
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(viewModel: StoreViewModel, navController: NavController) {
    val customers by viewModel.customers.collectAsState()
    val ledgers by viewModel.ledgers.collectAsState()
    val sales by viewModel.sales.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    
    val textColor = if (isDarkMode) Color(0xFFF3F4F6) else Color(0xFF1F2937)
    val grayColor = if (isDarkMode) Color(0xFF9AA4B2) else Color(0xFF6B7280)

    val filteredCustomers = customers.filter {
        it.name.contains(searchQuery, ignoreCase = true) || 
        (it.phone?.contains(searchQuery) == true)
    }

    var showAddModal by remember { mutableStateOf(false) }
    var selectedCustomer by remember { mutableStateOf<CustomerEntity?>(null) }
    var showCustomerDetail by remember { mutableStateOf(false) }
    var showLedgerModal by remember { mutableStateOf(false) }
    var ledgerModalType by remember { mutableStateOf("debt") }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
            Text("مشتریان و حساب‌ها", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
            IconButton(onClick = { 
                selectedCustomer = null
                showAddModal = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = textColor)
            }
        }

        // Search Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
            placeholder = { Text("جستجوی مشتری...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = grayColor) },
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = grayColor.copy(alpha = 0.3f),
                focusedBorderColor = Color(0xFF2563EB)
            )
        )

        if (customers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.People, contentDescription = null, tint = Color(0xFFC7D0DC), modifier = Modifier.size(48.dp).padding(bottom = 12.dp))
                Text("هنوز مشتری‌ای ثبت نکردی", color = grayColor, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(14.dp))
                Button(onClick = { showAddModal = true }, shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))) {
                    Text("افزودن اولین مشتری", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        } else if (filteredCustomers.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFC7D0DC), modifier = Modifier.size(48.dp).padding(bottom = 12.dp))
                Text("مشتری‌ای پیدا نشد", color = grayColor, fontSize = 14.sp)
            }
        } else {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                filteredCustomers.forEach { c ->
                    val customerLedgers = ledgers.filter { it.customerId == c.id }
                    val balance = customerLedgers.sumOf { if (it.type == "debt") it.amount else -it.amount }
                    
                    GlassCard(isDarkMode = isDarkMode, modifier = Modifier.padding(bottom = 10.dp), onClick = { 
                        selectedCustomer = c
                        showCustomerDetail = true
                    }) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(c.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = textColor)
                                Text(c.phone ?: "بدون شماره", fontSize = 12.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                            }
                            
                            val badgeBg = if (balance > 0) Color(0xFFFEE2E2) else if (balance < 0) Color(0xFFD1FAE5) else Color(0xFFF3F4F6)
                            val badgeColor = if (balance > 0) Color(0xFFEF4444) else if (balance < 0) Color(0xFF10B981) else Color(0xFF6B7280)
                            val badgeText = if (balance > 0) "بدهکار ${formatCurrencyFa(balance)}" else if (balance < 0) "طلبکار ${formatCurrencyFa(-balance)}" else "تسویه"
                            
                            Box(
                                modifier = Modifier.background(if(isDarkMode && balance > 0) Color(0xFF4a1414) else if(isDarkMode && balance < 0) Color(0xFF064e3b) else badgeBg, RoundedCornerShape(20.dp)).padding(horizontal = 10.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(badgeText, color = badgeColor, fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddModal) {
        ModalBottomSheet(
            onDismissRequest = { showAddModal = false },
            containerColor = if (isDarkMode) Color(0xFF1F2937) else Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                var cName by remember { mutableStateOf(selectedCustomer?.name ?: "") }
                var cPhone by remember { mutableStateOf(selectedCustomer?.phone ?: "") }
                var cNote by remember { mutableStateOf(selectedCustomer?.note ?: "") }

                Text(if (selectedCustomer == null) "مشتری جدید" else "ویرایش مشتری", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(bottom = 16.dp))

                Text("نام و نام خانوادگی", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = cName,
                    onValueChange = { cName = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Text("شماره تماس (اختیاری)", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = cPhone,
                    onValueChange = { cPhone = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Text("یادداشت (اختیاری)", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = cNote,
                    onValueChange = { cNote = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(14.dp)
                )

                Button(
                    onClick = {
                        if (cName.isNotBlank()) {
                            if (selectedCustomer == null) {
                                viewModel.addCustomer(CustomerEntity(id = UUID.randomUUID().toString(), name = cName, phone = cPhone.takeIf{it.isNotBlank()}, note = cNote.takeIf{it.isNotBlank()}, createdAt = getTodayYMD()))
                            } else {
                                val c = selectedCustomer!!.copy(name = cName, phone = cPhone.takeIf{it.isNotBlank()}, note = cNote.takeIf{it.isNotBlank()})
                                viewModel.updateCustomer(c)
                                selectedCustomer = c
                            }
                            showAddModal = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text("ذخیره", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                }
            }
        }
    }

    if (showCustomerDetail && selectedCustomer != null) {
        val c = selectedCustomer!!
        val customerLedgers = ledgers.filter { it.customerId == c.id }.sortedByDescending { it.date }
        val balance = customerLedgers.sumOf { if (it.type == "debt") it.amount else -it.amount }
        val customerSales = sales.filter { it.sale.customerId == c.id }.sortedByDescending { it.sale.date }

        ModalBottomSheet(
            onDismissRequest = { showCustomerDetail = false },
            containerColor = if (isDarkMode) Color(0xFF1F2937) else Color.White
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(c.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                        if (!c.phone.isNullOrBlank()) Text(c.phone, fontSize = 13.sp, color = grayColor, modifier = Modifier.padding(top = 4.dp))
                    }
                    IconButton(onClick = { showAddModal = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = grayColor)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Balance box
                val bg = if (balance > 0) Color(0xFFFEE2E2) else if (balance < 0) Color(0xFFD1FAE5) else Color(0xFFF3F4F6)
                val cColor = if (balance > 0) Color(0xFFEF4444) else if (balance < 0) Color(0xFF10B981) else Color(0xFF6B7280)
                val text = if (balance > 0) "بدهکار" else if (balance < 0) "طلبکار" else "تسویه"
                
                Box(modifier = Modifier.fillMaxWidth().background(if(isDarkMode && balance > 0) Color(0xFF4a1414) else if(isDarkMode && balance < 0) Color(0xFF064e3b) else bg, RoundedCornerShape(14.dp)).padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("وضعیت حساب", color = cColor, fontSize = 13.sp)
                        Text("$text ${formatCurrencyFa(kotlin.math.abs(balance))}", color = cColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                if (!c.note.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFFEF3C7).copy(alpha = if(isDarkMode) 0.1f else 1f), RoundedCornerShape(14.dp)).padding(12.dp)) {
                        Text("یادداشت: ${c.note}", color = if(isDarkMode) Color(0xFFFDE68A) else Color(0xFF92400E), fontSize = 12.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { ledgerModalType = "debt"; showLedgerModal = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                    ) {
                        Text("ثبت بدهی جدید", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = { ledgerModalType = "payment"; showLedgerModal = true },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("ثبت پرداخت", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                
                var tabIndex by remember { mutableStateOf(0) }
                TabRow(selectedTabIndex = tabIndex, containerColor = Color.Transparent, contentColor = Color(0xFF2563EB)) {
                    Tab(selected = tabIndex == 0, onClick = { tabIndex = 0 }) {
                        Text("دفتر حساب", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                    Tab(selected = tabIndex == 1, onClick = { tabIndex = 1 }) {
                        Text("تاریخچه خرید", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.Bold)
                    }
                }
                
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(top = 16.dp, bottom = 40.dp)) {
                    if (tabIndex == 0) {
                        if (customerLedgers.isEmpty()) {
                            Text("رکوردی ثبت نشده", color = grayColor, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        } else {
                            customerLedgers.forEach { l ->
                                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text(if(l.type == "debt") "بدهی: ${l.note ?: ""}" else "پرداختی: ${l.note ?: ""}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor)
                                        Text(l.date, fontSize = 11.5.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                                    }
                                    Text(
                                        text = "${if(l.type=="debt") "+" else "-"}${formatCurrencyFa(l.amount)}",
                                        fontWeight = FontWeight.ExtraBold, fontSize = 13.5.sp,
                                        color = if (l.type == "debt") Color(0xFFEF4444) else Color(0xFF10B981)
                                    )
                                }
                                HorizontalDivider(color = grayColor.copy(alpha = 0.1f))
                            }
                        }
                    } else {
                        if (customerSales.isEmpty()) {
                            Text("خریدی ثبت نشده", color = grayColor, fontSize = 13.sp, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        } else {
                            customerSales.forEach { s ->
                                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Column {
                                        Text("فاکتور ${s.sale.date.replace("-","/")}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor)
                                        Text("${formatNumberFa(s.items.size)} کالا", fontSize = 11.5.sp, color = grayColor, modifier = Modifier.padding(top = 2.dp))
                                    }
                                    Text(
                                        text = formatCurrencyFa(s.sale.finalTotal),
                                        fontWeight = FontWeight.ExtraBold, fontSize = 13.5.sp,
                                        color = textColor
                                    )
                                }
                                HorizontalDivider(color = grayColor.copy(alpha = 0.1f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showLedgerModal && selectedCustomer != null) {
        ModalBottomSheet(
            onDismissRequest = { showLedgerModal = false },
            containerColor = if (isDarkMode) Color(0xFF1F2937) else Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
                var lAmount by remember { mutableStateOf("") }
                var lNote by remember { mutableStateOf("") }
                var lDate by remember { mutableStateOf(getTodayYMD()) }

                Text(if (ledgerModalType == "debt") "ثبت بدهی جدید" else "ثبت پرداخت", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = textColor, modifier = Modifier.padding(bottom = 16.dp))

                Text("مبلغ (تومان)", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = lAmount,
                    onValueChange = { lAmount = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                Text("یادداشت / بابت", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                OutlinedTextField(
                    value = lNote,
                    onValueChange = { lNote = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                if (ledgerModalType == "debt") {
                    Text("تاریخ سررسید (اختیاری)", fontSize = 12.5.sp, color = grayColor, modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = lDate,
                        onValueChange = { lDate = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Button(
                    onClick = {
                        val amount = lAmount.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.addLedger(LedgerEntity(id = UUID.randomUUID().toString(), customerId = selectedCustomer!!.id, type = ledgerModalType, amount = amount, note = lNote, date = getTodayYMD(), dueDate = if(ledgerModalType=="debt") lDate else null))
                            showLedgerModal = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (ledgerModalType == "debt") Color(0xFFEF4444) else Color(0xFF10B981))
                ) {
                    Text("ثبت در دفتر", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.5.sp)
                }
            }
        }
    }
}
