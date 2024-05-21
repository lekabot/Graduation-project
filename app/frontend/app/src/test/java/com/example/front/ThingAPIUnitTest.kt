package com.example.front
import com.example.front.data.ThingAPI
import org.junit.Assert.assertEquals
import org.junit.Test


class ThingAPITest {
    private val api = ThingAPI()

    @Test
    fun testGetAllThing() {
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
                ThingAPI.Thing(id = 11, title = "Компутер")
            )
        )

        val actual = api.getAllThing()
        assertEquals(expected, actual)
    }
}
