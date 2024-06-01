package com.example.front.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.front.param_api
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import java.io.IOException
import java.nio.file.Path
import java.nio.file.Paths

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

    private suspend fun asyncExecuteRequest(request: Request): Response {
        return withContext(Dispatchers.IO + exceptionHandler) {
            try {
                client.newCall(request).execute()
            } catch (e: Exception) {
                println("Exception in asyncExecuteRequest: ${e.message}")
                throw e
            }
        }
    }

    fun executeRequest(request: Request): Response {
        val deferredResult = scope.async {
            asyncExecuteRequest(request)
        }

        return runBlocking {
            try {
                deferredResult.await()
            } catch (e: Exception) {
                println("Exception in executeRequest: ${e.message}")
                throw IOException("Error executing request", e)
            }
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

    fun stringToPath(str: String): Path {
        return Paths.get(str)
    }

    fun fileExists(path: String): Boolean {
        return param_api.getFile(stringToPath(path)) != null
    }

    fun convertToImage(responseBody: ResponseBody): Bitmap? {
        val bytes = responseBody.bytes()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    fun convertToBitmap(responseBody: ResponseBody?): Bitmap? {
        return responseBody?.byteStream()?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream)
        }
    }
}