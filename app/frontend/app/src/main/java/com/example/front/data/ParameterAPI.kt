package com.example.front.data

import com.example.front.data.Common.client
import com.example.front.data.Common.host
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.URLEncoder

class ParameterAPI() {
    data class ParameterList(val status: String, val data: MutableList<Parameter>)
    data class Parameter(val id: Int, val key: String, val value: String)

    data class OldParameter(val thingTitle: String, val key: String, val value: String)
    data class NewParameter(val key: String, val value: String)

    fun createParameter(thingTitle: String, key: String, value: String): Int {
        val requestUrl = "$host/parameter/parameter_create/$thingTitle"

        val json = """
        {
          "key": "$key",
          "value": "$value"
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Common.createRequest(requestUrl, "POST", requestBody)

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }

    fun getParameters(thingTitle: String): ParameterList {
        val encodedTitle = URLEncoder.encode(thingTitle, "UTF-8")
        val requestUrl = "$host/parameter/get_parameters_by_thing_name/$encodedTitle"

        val request = Common.createRequest(requestUrl, "GET")

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val parameterList = Gson().fromJson(response.body!!.string(), ParameterList::class.java)
        return parameterList
    }

    fun deleteParameter(thingTitle: String, key: String, value: String): Int {
        val requestUrl = "$host/parameter/delete_parameter"

        val json = """
        {
          "thing_title": "$thingTitle",
          "key": "$key",
          "value": "$value"
        }
        """.trimIndent()

        val requestBody = json.toRequestBody("application/json".toMediaType())

        val request = Common.createRequest(requestUrl, "DELETE", requestBody)

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }

    fun updateParameter(oldParameter: OldParameter, newParameter: NewParameter): Int {
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

        val request = Common.createRequest(requestUrl, "PUT", requestBody)

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }
}