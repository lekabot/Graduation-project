package com.example.front.data

import com.google.gson.Gson
import okhttp3.Cookie
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.IOException

class ThingAPI(private val host: String = "http://192.168.31.186:80") {

    private val client = OkHttpClient.Builder()
        .cookieJar(MyCookieJar())
        .build()
    data class ThingList(val status: String, val data: MutableList<Thing>)
    data class Thing(val id: Int, val title: String)

    fun getAllThing(): ThingList {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/thing/get_all_thing_for_user/"

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
            val thingList = Gson().fromJson(response.body!!.string(), ThingList::class.java)
            return thingList
        }
    }

    fun getThing(title: String): ThingList {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/thing/get_by_name/$title"

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
            val thing = Gson().fromJson(response.body!!.string(), ThingList::class.java)
            return thing
        }
    }

    fun addThing(title: String): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/thing/add_thing/$title"

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

    fun updateThingName(oldTitle: String, newTitle: String): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/thing/update_name/$oldTitle/$newTitle"

        val request = Request.Builder()
            .url(requestUrl)
            .put(RequestBody.create(null, ByteArray(0)))
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

    fun deleteThing(title: String): Int {
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val requestUrl = "$host/thing/delete_thing_by_title/$title"

        val request = Request.Builder()
            .url(requestUrl)
            .delete()
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

