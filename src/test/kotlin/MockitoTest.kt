import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Test
import org.mockito.Answers
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockitoSession
import org.mockito.MockitoSession
import org.mockito.kotlin.whenever
import org.mockito.kotlin.withSettings
import org.mockito.quality.Strictness
import org.mockito.kotlin.mock as mockWithDefaultAnswer


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MockitoTest {
    private lateinit var mockito: MockitoSession
    private lateinit var service: Service

    @BeforeAll
    fun setup() {
        service = mock()
        mockito = mockitoSession().initMocks(this).strictness(Strictness.STRICT_STUBS).startMocking()
    }

    @AfterAll
    fun teardown() {
        mockito.finishMocking()
    }

    @Test
    fun `returns default values even if not mocked`() {
        val serviceStrictStubbing = mock<Service>(withSettings().strictness(Strictness.STRICT_STUBS))
        val serviceReturnsSmartNulls = mock<Service>(withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS))
        val serviceReturnsMocks = mock<Service>(withSettings().defaultAnswer(Answers.RETURNS_MOCKS))
        val serviceReturnsDefaults = mock<Service>(withSettings().defaultAnswer(Answers.RETURNS_DEFAULTS))
        val serviceCallsRealMethods = mock<Service>(
            withSettings().defaultAnswer(
                Answers.CALLS_REAL_METHODS
            )
        )
        val serviceReturnsDeepStubs = mock<Service>(withSettings().defaultAnswer(Answers.RETURNS_DEEP_STUBS))
        val serviceReturnsSelf = mock<Service>(withSettings().defaultAnswer(Answers.RETURNS_SELF))

        assertThat(service(42)).isEqualTo(0)
        assertThat(serviceStrictStubbing(42)).isEqualTo(0)
        assertThat(serviceReturnsSmartNulls(42)).isEqualTo(0)
        assertThat(serviceReturnsMocks(42)).isEqualTo(0)
        assertThat(serviceReturnsDefaults(42)).isEqualTo(0)
        assertThat(serviceCallsRealMethods(42)).isEqualTo(0)
        assertThat(serviceReturnsDeepStubs(42)).isEqualTo(0)
        assertThat(serviceReturnsSelf(42)).isEqualTo(0)
    }

    private data object NotMockedException : IllegalStateException("not mocked")

    private fun notMocked(): Nothing {
        throw NotMockedException
    }

    private val serviceWithDefaultAnswer = mockWithDefaultAnswer<Service>(defaultAnswer = { notMocked() })

    @Test
    fun `throws exception if not mocked`() {
        assertThrows<NotMockedException> {
            serviceWithDefaultAnswer(42)
        }
    }

    @Test
    fun `does not throw exception when mocked`() {
        // this whenever will trigger the default answer leading to an exception before the test even has a chance to run
        whenever(serviceWithDefaultAnswer(42)).thenReturn(42)
        assertThat(serviceWithDefaultAnswer(42)).isEqualTo(42)
    }
}