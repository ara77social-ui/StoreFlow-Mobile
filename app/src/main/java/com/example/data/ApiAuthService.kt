package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ApiAuthService {
    // آدرس سرور با استفاده از IP مستقیم برای دور زدن مشکل DNS
    private val apiUrl = "http://193.141.65.207/api.php"
    private val hostName = "storeflow.ptteam.ir"

    private suspend fun rpcCallWithResult(action: String, jsonBody: org.json.JSONObject): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val url = java.net.URL("$apiUrl?action=$action")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Host", hostName) // ارسال هدر Host برای تشخیص سایت در دایرکت ادمین
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true

            java.io.OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                return@withContext conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private suspend fun rpcCall(action: String, jsonBody: org.json.JSONObject): Boolean {
        val res = rpcCallWithResult(action, jsonBody) ?: return false
        return res.trim() == "true"
    }

    suspend fun hasPassword(email: String): Boolean {
        val body = org.json.JSONObject().apply { put("p_email", email) }
        return rpcCall("has_password", body)
    }

    suspend fun login(email: String, pass: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_password", pass)
        }
        return rpcCall("login", body)
    }

    suspend fun signup(email: String, pass: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_password", pass)
            // ایمیل رو قبل از @ به عنوان یوزرنیم میفرستیم تا در بک‌اند استفاده بشه
            put("username", email.substringBefore("@")) 
        }
        return rpcCall("signup", body)
    }

    // این موارد هنوز در بک‌اند PHP پیاده‌سازی کامل نشده‌اند
    // اما برای اینکه ارور برنامه‌نویسی ندهند موقتا ساختار آن‌ها حفظ شده است.
    
    suspend fun changePassword(email: String, currentPwd: String, newPwd: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_current_password", currentPwd)
            put("p_new_password", newPwd)
        }
        return rpcCall("change_password", body)
    }

    suspend fun adminListUsers(adminEmail: String, adminPwd: String): org.json.JSONArray? {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
        }
        val res = rpcCallWithResult("admin_list_users", body) ?: return null
        return try { org.json.JSONArray(res) } catch(e: Exception) { null }
    }

    suspend fun adminSetSubscription(adminEmail: String, adminPwd: String, targetEmail: String, status: String, start: String?, end: String?): Boolean {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_email", targetEmail)
            put("p_status", status)
            put("p_start", start ?: org.json.JSONObject.NULL)
            put("p_end", end ?: org.json.JSONObject.NULL)
        }
        val res = rpcCallWithResult("admin_set_subscription", body)
        return res != null
    }

    suspend fun adminDeleteUser(adminEmail: String, adminPwd: String, targetEmail: String): Boolean {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_email", targetEmail)
        }
        val res = rpcCallWithResult("admin_delete_user", body)
        return res != null
    }

    suspend fun adminSetSupport(adminEmail: String, adminPwd: String, telegram: String, bale: String): Boolean {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_telegram", telegram)
            put("p_bale", bale)
        }
        val res = rpcCallWithResult("admin_set_support", body)
        return res != null
    }
}
