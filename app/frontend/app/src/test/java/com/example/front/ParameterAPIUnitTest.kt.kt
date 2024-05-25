package com.example.front
import com.example.front.data.AuthAPI
import com.example.front.data.ParameterAPI
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ParameterAPIUnitTest {
    private val paramAPI = ParameterAPI()
    private val authApi = AuthAPI()
    private val client = mockk<OkHttpClient>()
    private val response = mockk<Response>()

    @Test
    fun test1_create_parameter() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val expected = 200
        val actual = paramAPI.createParameter("Книга", "key", "value")
        assertEquals(expected, actual)
        authApi.logout()
    }

    @Test
    fun test2_get_parameter() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val expected = ParameterAPI.Parameter(id = 1, key = "Автор", value = "Джон Доу")
        val actual = paramAPI.getParameters("Книга")
        assertEquals(expected, actual.data[0])
        authApi.logout()
    }

    @Test
    fun test3_update_parameter() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val expected = 200
        val oldParam = ParameterAPI.OldParameter(thingTitle = "Книга", key = "key", value = "value")
        val newParam = ParameterAPI.NewParameter(key = "key_123", value = "value_123")
        val actual = paramAPI.updateParameter(oldParam, newParam)
        assertEquals(expected, actual)
        authApi.logout()
    }

    @Test
    fun test4_delete_parameter() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val expected = 200
        val actual = paramAPI.deleteParameter("Книга", "key_123", "value_123")
        assertEquals(expected, actual)
        authApi.logout()
    }
}
