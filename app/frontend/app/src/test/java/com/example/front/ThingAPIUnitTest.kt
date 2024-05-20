package com.example.front
import com.example.front.data.ThingAPI
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class GetAllThingTest {
    private val server = MockWebServer()
    private lateinit var tested: ThingAPI

    @Before
    fun setup() {
        server.start()
        tested = com.example.front.data.ThingAPI(
            server.url("/").toString()
        )
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun testGetAllThing() {
        val response = """
            {
                "status": 200,
                "data": [
                    {"id": 1, "title": "Книга"},
                    {"id": 2, "title": "Стол"}
                ]
            }
        """.trimIndent()

        server.enqueue(MockResponse().setBody(response))

        val result = tested.getAllThing()

        Assert.assertEquals(200, result?.status)
        Assert.assertEquals(1, result?.data?.get(0)?.id)
        Assert.assertEquals("Книга", result?.data?.get(0)?.title)
        Assert.assertEquals(2, result?.data?.get(1)?.id)
        Assert.assertEquals("Стол", result?.data?.get(1)?.title)
    }
}

class AddThingTest {
    private val server = MockWebServer()
    private lateinit var tested: ThingAPI

    @Before
    fun setup() {
        server.start()
        tested = ThingAPI(server.url("/").toString())
    }

    @After
    fun teardown() {
        server.shutdown()
    }

    @Test
    fun testAddThing() {
        val thingTitle = "New Thing"
        server.enqueue(MockResponse().setResponseCode(200))

        tested.addThing(thingTitle)

        val request = server.takeRequest()
        Assert.assertEquals("//thing/add_thing/New%20Thing", request.path)
    }
}