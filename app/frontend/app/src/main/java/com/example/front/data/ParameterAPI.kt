package com.example.front.data

import com.google.gson.Gson
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder

class ParameterAPI(private val host: String = "http://192.168.31.186:80") {
    private val client = OkHttpClient.Builder()
        .cookieJar(MyCookieJar())
        .build()

    data class ParameterList(val status: String, val data: MutableList<Parameter>)
    data class Parameter(val id: Int, val key: String, val value: String)

    data class OldParameter(val thingTitle: String, val key: String, val value: String)
    data class NewParameter(val key: String, val value: String)

    fun createParameter(thingTitle: String, key: String, value: String): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/parameter/parameter_create/$thingTitle"

        val json = """
        {
          "key": "$key",
          "value": "$value"
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(requestUrl)
            .post(requestBody)
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

    fun getParameters(thingTitle: String): ParameterList {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val encodedTitle = URLEncoder.encode(thingTitle, "UTF-8")
        val requestUrl = "$host/parameter/get_parameters_by_thing_name/$encodedTitle"

        val request = Request.Builder()
            .url(requestUrl)
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
            val parameterList = Gson().fromJson(response.body!!.string(), ParameterList::class.java)
            return parameterList
        }
    }

    fun deleteParameter(thingTitle: String, key: String, value: String): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/parameter/delete_parameter"

        val json = """
        {
          "thing_title": "$thingTitle",
          "key": "$key",
          "value": "$value"
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(requestUrl)
            .delete(requestBody)
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

    fun updateParameter(oldParameter: OldParameter, newParameter: NewParameter): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/parameter/update/"

        val json = """
        {
          "old_param": {
            "thing_title": "${oldParameter.thingTitle}",
            "key": "${oldParameter.key}",
            "value": "${oldParameter.value}"
          },
          "new_parameter": {
            "key": "${newParameter.key}",
            "value": "${newParameter.value}"
          }
          }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(requestUrl)
            .put(requestBody)
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
}