package com.example.ui

import android.content.Context
import androidx.compose.ui.platform.LocalContext

fun StoreViewModel.getSavedUserEmail(): String? {
    val prefs = getApplication<android.app.Application>().getSharedPreferences("store_prefs", Context.MODE_PRIVATE)
    return prefs.getString("user_email", null)
}

fun StoreViewModel.saveUserEmail(email: String?) {
    val prefs = getApplication<android.app.Application>().getSharedPreferences("store_prefs", Context.MODE_PRIVATE)
    prefs.edit().putString("user_email", email).apply()
}

suspend fun StoreViewModel.hasPassword(email: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.hasPassword(email)
}

suspend fun StoreViewModel.login(email: String, pass: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.login(email, pass)
}

suspend fun StoreViewModel.signup(email: String, pass: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.signup(email, pass)
}

suspend fun StoreViewModel.changePassword(email: String, currentPwd: String, newPwd: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.changePassword(email, currentPwd, newPwd)
}

suspend fun StoreViewModel.adminListUsers(adminEmail: String, adminPwd: String): org.json.JSONArray? {
    val auth = com.example.data.SupabaseAuthService()
    return auth.adminListUsers(adminEmail, adminPwd)
}

suspend fun StoreViewModel.adminSetSubscription(adminEmail: String, adminPwd: String, targetEmail: String, status: String, start: String?, end: String?): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.adminSetSubscription(adminEmail, adminPwd, targetEmail, status, start, end)
}

suspend fun StoreViewModel.adminDeleteUser(adminEmail: String, adminPwd: String, targetEmail: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.adminDeleteUser(adminEmail, adminPwd, targetEmail)
}

suspend fun StoreViewModel.adminSetSupport(adminEmail: String, adminPwd: String, telegram: String, bale: String): Boolean {
    val auth = com.example.data.SupabaseAuthService()
    return auth.adminSetSupport(adminEmail, adminPwd, telegram, bale)
}
