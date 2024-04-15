package com.example.front

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val message: String?, val status: String)
object ApiHelper {
    fun login(
        username: String,
        password: String
    ): Boolean {
        val url = "http://127.0.0.1:5000/api/v1.0/login"
        val client = OkHttpClient()
        val requestJson = Gson().toJson(LoginRequest(username, password))
        val requestBody = requestJson.toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val responseJson = response.body?.string()
                val responseObject = Gson().fromJson(responseJson, LoginResponse::class.java)
                responseObject?.status == "success"
            } else {
                false
            }
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            false
        }
    }
}

