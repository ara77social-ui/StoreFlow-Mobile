import re

with open("app/src/main/java/com/example/data/SupabaseAuthService.kt", "r") as f:
    content = f.read()

new_rpc = """
    private suspend fun rpcCallWithResult(functionName: String, jsonBody: JSONObject): String? = withContext(Dispatchers.IO) {
        try {
            val url = URL("$supabaseUrl/rest/v1/rpc/$functionName")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("apikey", anonKey)
            conn.setRequestProperty("Authorization", "Bearer $anonKey")
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true

            OutputStreamWriter(conn.outputStream).use { writer ->
                writer.write(jsonBody.toString())
                writer.flush()
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                return@withContext conn.inputStream.bufferedReader().use { it.readText() }
            } else {
                val errorString = conn.errorStream?.bufferedReader()?.use { it.readText() }
                println("Supabase Error: $responseCode - $errorString")
                return@withContext null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private suspend fun rpcCall(functionName: String, jsonBody: JSONObject): Boolean {
        val res = rpcCallWithResult(functionName, jsonBody) ?: return false
        return res.trim() == "true"
    }
"""

content = re.sub(r"private suspend fun rpcCall.*?return@withContext false\n        }\n    }", new_rpc, content, flags=re.DOTALL)

new_methods = """
    suspend fun changePassword(email: String, currentPwd: String, newPwd: String): Boolean {
        val body = JSONObject().apply { 
            put("p_email", email)
            put("p_current_password", currentPwd)
            put("p_new_password", newPwd)
        }
        return rpcCall("sf_change_password", body)
    }

    suspend fun adminListUsers(adminEmail: String, adminPwd: String): org.json.JSONArray? {
        val body = JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
        }
        val res = rpcCallWithResult("sf_admin_list_users", body) ?: return null
        return try { org.json.JSONArray(res) } catch(e: Exception) { null }
    }

    suspend fun adminSetSubscription(adminEmail: String, adminPwd: String, targetEmail: String, status: String, start: String?, end: String?): Boolean {
        val body = JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_email", targetEmail)
            put("p_status", status)
            put("p_start", start ?: JSONObject.NULL)
            put("p_end", end ?: JSONObject.NULL)
        }
        val res = rpcCallWithResult("sf_admin_set_subscription", body)
        return res != null
    }

    suspend fun adminDeleteUser(adminEmail: String, adminPwd: String, targetEmail: String): Boolean {
        val body = JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_email", targetEmail)
        }
        val res = rpcCallWithResult("sf_admin_delete_user", body)
        return res != null
    }

    suspend fun adminSetSupport(adminEmail: String, adminPwd: String, telegram: String, bale: String): Boolean {
        val body = JSONObject().apply {
            put("p_admin_email", adminEmail)
            put("p_admin_password", adminPwd)
            put("p_telegram", telegram)
            put("p_bale", bale)
        }
        val res = rpcCallWithResult("sf_admin_set_support", body)
        return res != null
    }
"""

content = content.replace("}\n", new_methods + "}\n")
content = content.replace("}\n" + new_methods + "}\n", new_methods + "}\n")

with open("app/src/main/java/com/example/data/SupabaseAuthService.kt", "w") as f:
    f.write(content)
