package com.example.front
import com.example.front.data.AuthAPI
import com.example.front.data.ThingAPI
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class ThingAPITest {
    private val api = ThingAPI()
    private val authApi = AuthAPI()
    private val client = mockk<OkHttpClient>()
    private val response = mockk<Response>()

    @Test
    fun test1_get_all_thing() {
        authApi.login(username = "admin", password = "admin")
        val expected = ThingAPI.ThingList(
            status = "success",
            data = mutableListOf(
                ThingAPI.Thing(id = 1, title = "Книга"),
                ThingAPI.Thing(id = 2, title = "Стол"),
                ThingAPI.Thing(id = 3, title = "Компьютер"),
                ThingAPI.Thing(id = 4, title = "Телефон"),
                ThingAPI.Thing(id = 5, title = "Часы"),
                ThingAPI.Thing(id = 6, title = "Картина"),
                ThingAPI.Thing(id = 7, title = "Стул"),
                ThingAPI.Thing(id = 8, title = "Лампа"),
                ThingAPI.Thing(id = 9, title = "Кошелек"),
                ThingAPI.Thing(id = 10, title = "Ключи"),
            )
        )

        val actual = api.getAllThing()
        assertEquals(expected, actual)
        authApi.logout()
    }

    @Test
    fun test2_get_thing() {
        authApi.login(username = "admin", password = "admin")
        val expected = ThingAPI.Thing(
            id = 1,
            title = "Книга"
        )
        val actual = api.getThing("Книга")
        assertEquals(expected, actual.data[0])
        authApi.logout()
    }


    @Test
    fun test3_add_thing_by_name() {
        authApi.login(username = "admin", password = "admin")

        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204

        val expected = 200
        val actual = api.addThing("test")
        assertEquals(expected, actual)
        authApi.logout()
    }

    @Test
    fun test4_update_thing_name() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val oldName = "test"
        val newName = "test2"
        val expected = 200
        val actual = api.updateThingName(oldName, newName)
        assertEquals(expected, actual)
        authApi.logout()
    }

    @Test
    fun test5_delete_thing() {
        authApi.login(username = "admin", password = "admin")
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 200
        val expected = 200
        val actual = api.deleteThing("test2")
        assertEquals(expected, actual)
        authApi.logout()
    }

}
