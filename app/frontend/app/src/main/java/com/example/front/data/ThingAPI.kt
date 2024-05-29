package com.example.front.data

import com.example.front.data.Common.client
import com.example.front.data.Common.createRequest
import com.example.front.data.Common.host
import com.google.gson.Gson
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.RequestBody
import okio.IOException

class ThingAPI() {
    data class ThingList(val status: String, val data: MutableList<Thing>)
    data class Thing(val id: Int, val title: String)

    fun getAllThing(): ThingList {
        val requestUrl = "$host/thing/get_all_thing_for_user/"
        val request = createRequest(requestUrl, "GET")
        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response =  Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val thingList = Gson().fromJson(response.body!!.string(), ThingList::class.java)
        return thingList
    }

     fun getThing(title: String): ThingList {
        val requestUrl = "$host/thing/get_by_name/$title"
        val request = createRequest(requestUrl, "GET")
        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        val thing = Gson().fromJson(response.body!!.string(), ThingList::class.java)
        return thing
    }

    fun addThing(title: String): Int {
        val requestUrl = "$host/thing/add_thing/$title"

        val request = createRequest(requestUrl, "POST",
            RequestBody.create(null, ByteArray(0)))

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }

    fun updateThingName(oldTitle: String, newTitle: String): Int {
        val requestUrl = "$host/thing/update_name/$oldTitle/$newTitle"

        val request = createRequest(requestUrl, "PUT",
            RequestBody.create(null, ByteArray(0)))

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }

    fun deleteThing(title: String): Int {
        val requestUrl = "$host/thing/delete_thing_by_title/$title"

        val request = createRequest(requestUrl, "DELETE")

        val cookie = Common.formCookie(requestUrl)

        client.cookieJar.saveFromResponse(requestUrl.toHttpUrlOrNull() ?: throw IOException("Host not found"), listOf(cookie))

        val response = Common.executeRequest(request)
        if (!response.isSuccessful) throw IOException("Unexpected code $response")
        return response.code
    }
}

