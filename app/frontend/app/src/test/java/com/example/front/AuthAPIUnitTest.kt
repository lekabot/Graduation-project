package com.example.front

import com.example.front.data.AuthAPI
import com.example.front.data.TokenManager
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters
import java.io.IOException

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
internal class AuthAPITest {
    private val api = AuthAPI()
    private val client = mockk<OkHttpClient>()
    private val response = mockk<Response>()
    @Test
    fun test1_login_success() {

        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204

        val result = api.login(username = "admin", password = "admin")
        assertTrue(result)
    }


    @Test
    fun test2_login_failure() {
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns false

        val result = api.login("username", "password")
        assertFalse(result)
    }

    @Test
    fun test3_successful_registration() {
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204

        val result = api.register("55", "55", "55")
        assertEquals(201, result)

    }

    @Test
    fun test4_user_already_exists() {
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns false
        every { response.code } returns 400

        val result = api.register("55", "55", "55")
        assertEquals(400, result)
    }

    @Test
    fun test5_delete_user() {
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204
        val result = api.deleteByUsername("55")
        assertEquals(204, result)
    }

    @Test
    fun test6_check_user_is_not_auth() {
        api.logout()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val token = TokenManager.getToken() ?: throw IOException("Token not found")
        val result = api.checkUserIsAuth(token)
        assertEquals(200, result)
    }

    @Test
    fun test7_check_user_is_not_auth() {
        api.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 401
        val result = api.checkUserIsAuth()
        assertEquals(401, result)
        api.logout()
    }
}
