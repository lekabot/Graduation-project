package com.example.front.data

import okhttp3.Call
import okhttp3.Callback
import okhttp3.FormBody
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.BufferedSink
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


    sealed class RegistrationResult {
        data object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    fun register(
        email: String,
        username: String,
        password: String,
    ): RegistrationResult {
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
            if (response.isSuccessful) {
                RegistrationResult.Success
            } else if (response.code == 400) {
                RegistrationResult.Error("Такой пользователь уже существует")
            } else {
                RegistrationResult.Error("Неизвестная ошибка")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            RegistrationResult.Error(e.message ?: "Неизвестная ошибка")
        }
    }

    fun delete_by_username(username: String): Int {
        val client = OkHttpClient()
        val url = "http://192.168.31.186:1234/user/delete_by_username/$username"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.code
        }
    }
}

