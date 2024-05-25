package com.example.front.data

import java.io.File

object TokenManager {
    private var token: String? = null
    private const val TOKEN_FILE = "token.txt"

    fun setToken(token: String?) {
        this.token = token
        token?.let {
            try {
                File(TOKEN_FILE).writeText(it)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun getToken(): String? {
        if (token == null) {
            token = try {
                File(TOKEN_FILE).readText()
            } catch (e: Exception) {
                null
            }
        }
        return token
    }
}