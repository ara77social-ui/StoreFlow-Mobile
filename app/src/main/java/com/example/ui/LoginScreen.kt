package com.example.ui

import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

enum class LoginStep { Email, Password, CreatePassword, Forgot }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: StoreViewModel,
    onLoginSuccess: (String) -> Unit,
    onBiometricLoginRequest: () -> Unit,
    showBiometricOption: Boolean
) {
    var currentStep by remember { mutableStateOf(LoginStep.Email) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    
    val coroutineScope = rememberCoroutineScope()
    
    val isDarkMode = viewModel.isDarkMode.collectAsState().value
    
    val backgroundBrush = if (isDarkMode) {
        Brush.linearGradient(
            colors = listOf(Color(0xFF12161F), Color(0xFF1B2028))
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color(0xFF2563EB), Color(0xFF1d4fd1))
        )
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .background(Color.White.copy(alpha = 0.16f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(42.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            when (currentStep) {
                LoginStep.Email -> {
                    Text("ورود به StoreFlow", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("ایمیلت رو وارد کن", fontSize = 13.5.sp, color = Color.White.copy(alpha = 0.85f), modifier = Modifier.padding(bottom = 22.dp))
                    
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        placeholder = { Text("you@example.com") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = Color(0xFF3B82F6)
                        )
                    )
                    
                    Button(
                        onClick = {
                            if (email.isBlank() || !email.contains("@")) {
                                errorMessage = "یک ایمیل معتبر وارد کن"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                val hasPwd = viewModel.hasPassword(email.trim().lowercase())
                                isLoading = false
                                if (hasPwd) {
                                    currentStep = LoginStep.Password
                                } else {
                                    currentStep = LoginStep.CreatePassword
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.14f)),
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) "در حال بررسی..." else "ورود", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color(0xFFFEE2E2), fontSize = 12.5.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    
                    if (showBiometricOption) {
                        Spacer(modifier = Modifier.height(14.dp))
                        OutlinedButton(
                            onClick = onBiometricLoginRequest,
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                        ) {
                            Icon(Icons.Default.Fingerprint, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ورود با اثر انگشت", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                LoginStep.Password -> {
                    Text("ورود امن", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(email, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        placeholder = { Text("رمز عبور StoreFlow") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    Button(
                        onClick = {
                            if (password.isBlank()) {
                                errorMessage = "رمز عبور رو وارد کن"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                val ok = viewModel.login(email.trim().lowercase(), password)
                                isLoading = false
                                if (ok) {
                                    onLoginSuccess(email.trim().lowercase())
                                } else {
                                    errorMessage = "رمز عبور اشتباه است"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.14f)),
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) "در حال بررسی..." else "ورود", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color(0xFFFEE2E2), fontSize = 12.5.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        "رمز عبورت رو فراموش کردی؟",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { currentStep = LoginStep.Forgot }.padding(8.dp)
                    )
                    Text(
                        "ورود با ایمیل دیگر",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { currentStep = LoginStep.Email }.padding(8.dp)
                    )
                }
                
                LoginStep.CreatePassword -> {
                    Text("ساخت رمز عبور", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(email, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    
                    Text("این اولین ورودته؛ یک رمز عبور برای StoreFlow بساز", color = Color.White, fontSize = 13.5.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(bottom = 10.dp))
                    
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        placeholder = { Text("رمز عبور جدید (حداقل ۶ کاراکتر)") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        placeholder = { Text("تکرار رمز عبور") },
                        singleLine = true,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(imageVector = image, contentDescription = null)
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    Text("⚠️ این رمز عبور رو جایی امن (یادداشت گوشی، کاغذ و…) بنویس. اگه فراموشش کنی، امکان بازیابی خودکار وجود نداره و فقط از طریق پشتیبانی قابل حل‌شدنه.", color = Color(0xFFEF4444), fontSize = 11.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(vertical = 12.dp))
                    
                    Button(
                        onClick = {
                            if (password.length < 6) {
                                errorMessage = "رمز عبور باید حداقل ۶ کاراکتر باشه"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                errorMessage = "رمز عبور و تکرارش یکسان نیستند"
                                return@Button
                            }
                            isLoading = true
                            errorMessage = ""
                            coroutineScope.launch {
                                val ok = viewModel.signup(email.trim().lowercase(), password)
                                isLoading = false
                                if (ok) {
                                    onLoginSuccess(email.trim().lowercase())
                                } else {
                                    errorMessage = "ذخیره رمز عبور با خطا مواجه شد؛ اتصال اینترنت رو چک کن"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha=0.14f)),
                        enabled = !isLoading
                    ) {
                        Text(if (isLoading) "در حال ساخت..." else "ساخت رمز و ورود", color = Color.White, fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color(0xFFFEE2E2), fontSize = 12.5.sp, modifier = Modifier.padding(top = 8.dp))
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "ورود با ایمیل دیگر",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { currentStep = LoginStep.Email }.padding(8.dp)
                    )
                }
                
                LoginStep.Forgot -> {
                    Text("بازیابی رمز عبور", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(bottom = 16.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .padding(bottom = 16.dp)
                    ) {
                        Text(email, fontSize = 12.5.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    
                    Text(
                        "امکان بازیابی خودکار رمز عبور وجود نداره.\nبرای بازیابی حساب، از طریق راه‌های زیر با پشتیبانی تماس بگیر.",
                        color = Color.White,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(bottom = 14.dp)
                    )
                    
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFDCE7FF), RoundedCornerShape(12.dp)).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("@Abol_2888 — تلگرام", color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB), fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Box(
                        modifier = Modifier.fillMaxWidth().background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp)).border(1.dp, Color(0xFFDCE7FF), RoundedCornerShape(12.dp)).padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("@abol2066 — بله", color = if (isDarkMode) Color(0xFF60A5FA) else Color(0xFF2563EB), fontSize = 14.5.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "بازگشت به ورود",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { currentStep = LoginStep.Password }.padding(8.dp)
                    )
                }
            }
        }
    }
}
}
