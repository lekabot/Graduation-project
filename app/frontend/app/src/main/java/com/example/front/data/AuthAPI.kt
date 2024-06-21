package com.example.front.data

import com.example.front.data.Common.client
import com.example.front.data.Common.createRequest
import com.example.front.data.Common.executeRequest
import com.example.front.data.Common.formCookie
import com.example.front.data.Common.host
import okhttp3.Cookie
import okhttp3.FormBody
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException


class AuthAPI() {



    fun login(username: String, password: String): Boolean {
        val url = "$host/auth/login"
        val formBody = FormBody.Builder()
            .add("username", username)
            .add("password", password)
            .build()

        val request = createRequest(url, "POST", formBody)
        val response = executeRequest(request)


        if (!response.isSuccessful) {
            return false
        }

        val cookies = response.headers("Set-Cookie")

        var token: String?
        cookies.forEach { cookie ->
            if (cookie.startsWith("bonds=")) {
                token = cookie.substringAfter("bonds=").substringBefore(";")
                TokenManager.setToken(token!!)
            }
        }
        return true
    }


    fun logout(): Int {

        val requestUrl = "$host/auth/logout"
        val request = createRequest(requestUrl, "POST",
            RequestBody.create(null, ByteArray(0)))
        val cookie = formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = executeRequest(request)
        return response.code
    }

    fun checkServCon(): Int {
        val requestUrl = "$host/auth/jwt/token"
        val request = createRequest(requestUrl, "GET")
        val cookie = formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = executeRequest(request)
        return response.code
    }

    fun register(
        email: String,
        username: String,
        password: String,
    ): Int {
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

        val request = createRequest(url, "POST", requestBody)

        val response = executeRequest(request)
        return response.code
    }

    fun deleteByUsername(username: String): Int {
        val url = "$host/user/delete_by_username/$username"
        val request = createRequest(url, "DELETE")

        val response = executeRequest(request)
        return response.code
    }

    fun checkUserIsAuth(token: String? = null): Int {
        val requestUrl = "$host/protected-route"
        val request = createRequest(requestUrl, "GET")

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

