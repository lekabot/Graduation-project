package com.example.front.data

import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class AuthAPI(private val host: String = "http://192.168.31.186:80") {

    private val client = OkHttpClient.Builder()
        .cookieJar(MyCookieJar())
        .build()


    sealed class RegistrationResult {
        data object Success : RegistrationResult()
        data class Error(val message: String) : RegistrationResult()
    }

    fun login(username: String, password: String): Boolean {
        val url = "$host/auth/login"
        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = Request.Builder()
            .url(url)
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            return false
        }

        val cookies = response.headers("Set-Cookie")

        var token: String? = null
        cookies.forEach { cookie ->
            if (cookie.startsWith("bonds=")) {
                token = cookie.substringAfter("bonds=").substringBefore(";")
                TokenManager.setToken(token!!)
                return@forEach
            }
        }
        return true
    }

    fun logout(): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")

        val requestUrl = "$host/auth/logout"
        val request = Request.Builder()
            .url(requestUrl)
            .post(RequestBody.create(null, ByteArray(0)))
            .build()

        val cookie = Cookie.Builder()
            .domain(requestUrl.toHttpUrlOrNull()?.host ?: throw IOException("Host not found"))
            .path("/")
            .name("bonds")
            .value(token)
            .httpOnly()
            .build()

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.code
        }
    }


    fun register(
        email: String,
        username: String,
        password: String,
    ): RegistrationResult {
        val url = "$host/auth/register"

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

    fun deleteByUsername(username: String): Int {
        val url = "$host/user/delete_by_username/$username"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return response.code
        }
    }

    fun checkUserIsAuth(token: String? = null): Int {
        val requestUrl = "$host/protected-route"
        val request = Request.Builder()
            .url(requestUrl)
            .get()
            .build()

        val cookie = Cookie.Builder()
            .domain(requestUrl.toHttpUrlOrNull()?.host ?: throw IOException("Host not found"))
            .path("/")
            .name("bonds")
            .value(token ?: "")
            .httpOnly()
            .build()

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        client.newCall(request).execute().use { response ->
            return response.code
        }
    }
}

