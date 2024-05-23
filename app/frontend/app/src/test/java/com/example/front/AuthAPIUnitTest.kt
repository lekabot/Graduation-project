
import com.example.front.data.AuthAPI
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

internal class AuthAPITest {
    @Test
    fun `login success`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204

        val result = AuthAPI.login(username = "admin", password = "admin", host = "http://localhost:80")
        assertTrue(result)
    }


    @Test
    fun `login failure`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns false

        val result = AuthAPI.login("username", "password")
        assertFalse(result)
    }

    @Test
    fun `test successful registration`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.code } returns 204

        val result = AuthAPI.register("55", "55", "55")
        assertEquals(AuthAPI.RegistrationResult.Success, result)

    }

    @Test
    fun `test user already exists`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns false
        every { response.code } returns 400

        val result = AuthAPI.register("email", "username", "password")
        assertEquals(AuthAPI.RegistrationResult.Error("Такой пользователь уже существует"), result)
    }

//    @Test
//    fun `delete user test`() {
//        val client = mockk<OkHttpClient>()
//        val response = mockk<Response>()
//        every { client.newCall(any()).execute() } returns response
//        every { response.isSuccessful } returns true
//        every { response.code } returns 204
//        val result = AuthAPI.delete_by_username("55")
//        assertEquals(204, result)
//    }
}
