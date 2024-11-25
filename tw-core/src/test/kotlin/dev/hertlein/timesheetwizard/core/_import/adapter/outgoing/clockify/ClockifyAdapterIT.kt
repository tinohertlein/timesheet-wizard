package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.ClockifyAdapter
import dev.hertlein.timesheetwizard.core.shared.configloader.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core.shared.configloader.CustomerConfigLoader
import dev.hertlein.timesheetwizard.core.shared.configloader.ExportConfigLoader
import dev.hertlein.timesheetwizard.core.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.Customer.Id
import dev.hertlein.timesheetwizard.core.shared.model.Customer.Name
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import dev.hertlein.timesheetwizard.core.util.TestApplication
import org.apache.http.entity.ContentType
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.mockito.Mockito.`when`
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.verify.VerificationTimes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

private const val MOCK_SERVER_HOST = "http://localhost"
private const val MOCK_SERVER_PORT = 1082
private const val AN_API_KEY = "an-api-key"
private const val A_WORKSPACE_ID = "a-workspace-id"

@DisplayName("ClockifyAdapter")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    classes = [TestApplication::class],
    properties = [
        "timesheet-wizard.import.clockify.reports-url=$MOCK_SERVER_HOST:$MOCK_SERVER_PORT",
        "timesheet-wizard.import.clockify.api-key=$AN_API_KEY",
        "timesheet-wizard.import.clockify.workspace-id=$A_WORKSPACE_ID",
    ]
)
class ClockifyAdapterIT {

    private lateinit var mockServer: ClientAndServer

    @Autowired
    private lateinit var clockifyImportAdapter: ClockifyAdapter

    @MockitoBean(extraInterfaces = [ExportConfigLoader::class, CustomerConfigLoader::class])
    private lateinit var clockifyIdsLoader: ClockifyIdsLoader

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
    }

    @BeforeEach
    fun setup() {
        `when`(clockifyIdsLoader.loadClockifyIds())
            .thenReturn(listOf(ClockifyId("1000", "62dd35202849d633796f5459")))
    }

    @Test
    fun `should import a timesheet`() {
        val aCustomer = Customer(Id("1000"), Name("PiedPiper"), true)
        val startDate = LocalDate.of(2022, 1, 1)
        val endDate = LocalDate.of(2022, 12, 31)
        prepareClockifyServer(
            "first_clockify_response.json",
            "second_clockify_response.json",
            "third_clockify_response.json"
        )

        val timesheet = clockifyImportAdapter.fetchTimesheet(aCustomer, startDate..endDate)

        mockServer.verify(HttpRequest.request(), VerificationTimes.exactly(3))
        SoftAssertions().apply {
            assertThat(timesheet).isNotNull
            assertThat(timesheet?.customer).isEqualTo(aCustomer)
            assertThat(timesheet?.dateRange?.start).isEqualTo(startDate)
            assertThat(timesheet?.dateRange?.endInclusive).isEqualTo(endDate)
            assertThat(timesheet?.entries?.get(0)?.duration).isEqualTo(9.hours)
            assertThat(timesheet?.entries?.get(0)?.tags).containsExactly(Timesheet.Entry.Tag("Remote"))
            assertThat(timesheet?.entries?.get(1)?.duration).isEqualTo(9.hours)
            assertThat(timesheet?.entries?.get(1)?.tags).isEmpty()
        }.assertAll()
    }

    private fun prepareClockifyServer(vararg responseFiles: String) {
        val request = HttpRequest.request()
            .withMethod("POST")
            .withPath("/workspaces/$A_WORKSPACE_ID/reports/detailed")
            .withHeader("X-Api-Key", AN_API_KEY)
            .withHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.mimeType)

        responseFiles.forEach {
            mockServer.`when`(request, Times.exactly(1))
                .respond(
                    HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(ResourcesReader.stringFromResourceFile("${this.javaClass.packageName}/$it"))
                        .withDelay(TimeUnit.MILLISECONDS, 500)
                        .withHeaders(
                            Headers(
                                Header.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                            )
                        )
                )
        }
    }
}