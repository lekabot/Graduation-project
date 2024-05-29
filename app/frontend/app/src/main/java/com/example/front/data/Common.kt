package com.example.front.data

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException

object Common {
    const val host: String = "http://192.168.31.186:80"
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throw IOException("Handle exception: ${throwable.message}")
    }
    private val supervisorJob = SupervisorJob()
    val scope = CoroutineScope(Dispatchers.IO + supervisorJob + exceptionHandler)

    val client = OkHttpClient.Builder()
        .cookieJar(MyCookieJar())
        .build()


    fun createRequest(url: String, method: String, body: RequestBody? = null): Request {
        return Request.Builder()
            .url(url)
            .method(method, body)
            .build()
    }

    suspend fun asyncExecuteRequest(request: Request): Response {
        return scope.async {
            client.newCall(request).execute()
        }.await()
    }

    fun executeRequest(request: Request): Response {
        val deferredResult = CoroutineScope(Dispatchers.IO).async {
            asyncExecuteRequest(request)
        }

        return runBlocking {
            deferredResult.await()
        }
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