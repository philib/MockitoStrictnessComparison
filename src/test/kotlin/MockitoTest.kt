import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.mock
import org.mockito.stubbing.Answer


class NotMockedException : IllegalStateException("not mocked")

class ThrowingDefaultAnswer : Answer<Any> {
    override fun answer(invocation: org.mockito.invocation.InvocationOnMock?) {
        throw NotMockedException()
    }
}

inline fun <reified T : Any>mockStrictly(): T {
    return mock<T>(defaultAnswer = ThrowingDefaultAnswer())
}

@ExtendWith(MockitoExtension::class)
class MockitoTest {

    private data object NotMockedException : IllegalStateException("not mocked")

    @Mock
    private lateinit var serviceMockedWithAnnotation: Service

    private lateinit var serviceMockedStrictly: Service

    @BeforeEach
    fun setup() {
        serviceMockedStrictly = mockStrictly<Service>()
    }


    // Part 1: Mocking with @Mock annotation is not strict enough
    @Test
    fun `@Mock annotated mock fails if parameters do not match`() {
        whenever(serviceMockedWithAnnotation(43)).thenReturn(12)

        assertThat(serviceMockedWithAnnotation(42)).isEqualTo(0)
    }

    @Test
    fun `@Mock annotated mock returns default values even if not mocked - This is not as strict as we want it`() {
        serviceMockedWithAnnotation
        assertThat(serviceMockedWithAnnotation(42)).isEqualTo(0)
    }

    // Part 2: Mocking with mockStrictly() is strict

    @Test
    fun `does fail because mocked stub is not called`() {
            Mockito.doReturn(42).whenever(serviceMockedStrictly)(42)
    }


    @Test
    fun `mock with default throws if parameters do not match - test fails because mock was not called`() {
        assertThrows<NotMockedException> {
            Mockito.doReturn(42).whenever(serviceMockedStrictly)(42)
            assertThat(serviceMockedStrictly(43)).isEqualTo(42)
        }
    }

    @Test
    fun `mock with default fails by thrown exception if a method on mock is called and mocking was forgotten`() {
            serviceMockedStrictly(42)
    }

    @Test
    fun `mock with default does not throw exception when a method is called that was explicitly mocked`() {
        Mockito.doReturn(42).whenever(serviceMockedStrictly)(42)
        assertThat(serviceMockedStrictly(42)).isEqualTo(42)
    }
}
