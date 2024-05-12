package com.example.front.data

import com.google.gson.Gson
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object ApiHelper {
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
}

