package com.example.front.data

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException

class ThingAPI(val host: String = "http://192.168.31.186:1234") {

    private val client = OkHttpClient()
    data class ThingList(val status: Int, val data: MutableList<Thing>)
    data class Thing(val id: Int, val title: String)
    fun getAllThing(): ThingList? {
        val request = Request.Builder()
            .url("$host/thing/get_all_thing_for_user/")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            val thingList = Gson().fromJson(response.body!!.string(), ThingList::class.java)
            return thingList
        }
    }

    fun addThing(title: String) {
        val request = Request.Builder()
            .url("$host/thing/add_thing/$title")
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
        }
    }
}

