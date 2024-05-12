package com.example.front.data

import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object AuthAPI {
    fun login(
        username: String,
        password: String
    ): Boolean {
        val url = "http://192.168.31.186:1234/auth/login"
        val client = OkHttpClient()

        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: IOException) {
            false
        } catch (e: Exception) {
            false
        }
    }

    fun register(
        email: String,
        username: String,
        password: String
    ): Boolean {
        val client = OkHttpClient()
        val url = "http://192.168.31.186:1234/auth/register"

        val json = JSONObject()
        json.put("email", email)
        json.put("username", username)
        json.put("password", password)
        json.put("is_active", true)
        json.put("is_superuser", false)
        json.put("is_verified", false)

        val requestBody = json.toString().toRequestBody(
            "application/json; charset=utf-8".toMediaType())

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        return try {
            val response = client.newCall(request).execute()
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

