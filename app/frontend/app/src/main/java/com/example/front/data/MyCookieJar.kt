package com.example.front.data

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class MyCookieJar : CookieJar {
    private val cookieStore: MutableMap<HttpUrl, List<Cookie>> = mutableMapOf()

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        cookieStore[url] = cookies
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return cookieStore[url] ?: listOf()
    }
}