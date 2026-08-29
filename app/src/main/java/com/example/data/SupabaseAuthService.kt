package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class SupabaseAuthService {
    private val supabaseUrl = "https://hthlwgxchrqcesbykarz.supabase.co"
    private val anonKey = "sb_publishable_JJuV69auIsjiUhJg9yqJRA_3d5ecNSR"

    private suspend fun rpcCallWithResult(functionName: String, jsonBody: org.json.JSONObject): String? = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val url = java.net.URL("$supabaseUrl/rest/v1/rpc/$functionName")
            val conn = url.openConnection() as java.net.HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("apikey", anonKey)
            conn.setRequestProperty("Authorization", "Bearer $anonKey")
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

    private suspend fun rpcCall(functionName: String, jsonBody: org.json.JSONObject): Boolean {
        val res = rpcCallWithResult(functionName, jsonBody) ?: return false
        return res.trim() == "true"
    }

    suspend fun changePassword(email: String, currentPwd: String, newPwd: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_current_password", currentPwd)
            put("p_new_password", newPwd)
        }
        return rpcCall("sf_change_password", body)
    }

    suspend fun adminListUsers(adminEmail: String, adminPwd: String): org.json.JSONArray? {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
        }
        val res = rpcCallWithResult("sf_admin_list_users", body) ?: return null
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
        val res = rpcCallWithResult("sf_admin_set_subscription", body)
        return res != null
    }

    suspend fun adminDeleteUser(adminEmail: String, adminPwd: String, targetEmail: String): Boolean {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_email", targetEmail)
        }
        val res = rpcCallWithResult("sf_admin_delete_user", body)
        return res != null
    }

    suspend fun adminSetSupport(adminEmail: String, adminPwd: String, telegram: String, bale: String): Boolean {
        val body = org.json.JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_telegram", telegram)
            put("p_bale", bale)
        }
        val res = rpcCallWithResult("sf_admin_set_support", body)
        return res != null
    }

    suspend fun hasPassword(email: String): Boolean {
        val body = org.json.JSONObject().apply { put("p_email", email) }
        return rpcCall("sf_has_password", body)
    }

    suspend fun login(email: String, pass: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_password", pass)
        }
        return rpcCall("sf_login", body)
    }

    suspend fun signup(email: String, pass: String): Boolean {
        val body = org.json.JSONObject().apply { 
            put("p_email", email)
            put("p_password", pass)
        }
        return rpcCall("sf_signup", body)
    }
}
