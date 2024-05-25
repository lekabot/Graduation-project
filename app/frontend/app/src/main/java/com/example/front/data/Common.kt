package com.example.front.data

import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

object Common {
    const val host: String = "http://10.199.181.225:80"

    val client = OkHttpClient.Builder()
        .cookieJar(MyCookieJar())
        .build()


    fun createRequest(url: String, method: String, body: RequestBody? = null): Request {
        return Request.Builder()
            .url(url)
            .method(method, body)
            .build()
    }

    fun executeRequest(request: Request): Response {
        return client.newCall(request).execute()
    }

    fun formCookie(requestUrl: String): Cookie {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")

        val cookie = Cookie.Builder()
            .domain(requestUrl.toHttpUrlOrNull()?.host ?: throw IOException("Host not found"))
            .path("/")
            .name("bonds")
            .value(token)
            .httpOnly()
            .build()

        return cookie
    }
}