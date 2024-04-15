import com.android.volley.*
import com.example.front.ApiHelper
import io.mockk.*
import okhttp3.OkHttpClient
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.Assert.*
import okhttp3.Response
class ApiHelperTest {
    @Test
    fun `login success`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns true
        every { response.body?.string() } returns "{\"message\":\"success\",\"status\":\"success\"}"

        val result = ApiHelper.login("user1", "1")
        assertTrue(result)
    }

    @Test
    fun `login failure`() {
        val client = mockk<OkHttpClient>()
        val response = mockk<Response>()
        every { client.newCall(any()).execute() } returns response
        every { response.isSuccessful } returns false

        val result = ApiHelper.login("username", "password")
        assertFalse(result)
    }
}
