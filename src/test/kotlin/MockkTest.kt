import io.mockk.MockKException
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class MockkTest {
    private val service = mockk<Service>()

    @Test
    fun `throws exception if not mocked`() {
        assertThrows<MockKException> {
            assertThat(service(42)).isEqualTo(0)
        }
    }

    @Test
    fun `throws exception if mocked differently`() {
        every { service(43) } returns 42
        assertThrows<MockKException> {
            assertThat(service(42)).isEqualTo(0)
        }
    }

    @Test
    fun `service returns mocked response`() {
        every { service(42) } returns 0
        assertThat(service(42)).isEqualTo(0)
    }

}