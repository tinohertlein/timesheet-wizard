package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify

import com.google.common.net.HttpHeaders
import com.google.common.net.MediaType
import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config.ClockifyId
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.HttpReportClient
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.RequestBodyFactory
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.ResponseBodyMapper
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer.Id
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer.Name
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.verify.VerificationTimes
import java.net.http.HttpClient
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours

private const val MOCK_SERVER_HOST = "http://localhost"
private const val MOCK_SERVER_PORT = 1082
private const val AN_API_KEY = "an-api-key"
private const val A_WORKSPACE_ID = "a-workspace-id"

@DisplayName("ClockifyAdapter")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClockifyAdapterTest {

    private lateinit var mockServer: ClientAndServer
    private lateinit var clockifyAdapter: ClockifyAdapter

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
    }

    @BeforeEach
    fun beforeEach() {
        val clockifyIdsLoader: ClockifyIdsLoader = mockk()
        val clockifyConfig: ClockifyConfig = mockk()

        every { clockifyIdsLoader.loadClockifyIds() } returns listOf(ClockifyId(TestFixture.Import.aCustomer.id.value, ""))
        every { clockifyConfig.reportsUrl } returns "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT"
        every { clockifyConfig.apiKey } returns AN_API_KEY
        every { clockifyConfig.workspaceId } returns A_WORKSPACE_ID

        clockifyAdapter = ClockifyAdapter(
            clockifyIdsLoader,
            HttpReportClient(clockifyConfig, HttpClient.newHttpClient(), TestFixture.App.objectMapper),
            RequestBodyFactory(),
            ResponseBodyMapper()
        )
    }

    @Test
    fun `should import a timesheet`() {
        prepareClockifyServer(
            "first_clockify_response.json",
            "second_clockify_response.json",
            "third_clockify_response.json"
        )

        val timesheet = clockifyAdapter.fetchTimesheet(TestFixture.Import.aCustomer, TestFixture.Import.aDateRange)

        mockServer.verify(HttpRequest.request(), VerificationTimes.exactly(3))
        SoftAssertions().apply {
            assertThat(timesheet).isNotNull
            assertThat(timesheet.customer.id.value).isEqualTo(TestFixture.Import.aCustomer.id.value)
            assertThat(timesheet.customer.name.value).isEqualTo(TestFixture.Import.aCustomer.name.value)
            assertThat(timesheet.dateRange.start).isEqualTo(TestFixture.Import.aDateRange.start)
            assertThat(timesheet.dateRange.endInclusive).isEqualTo(TestFixture.Import.aDateRange.endInclusive)
            assertThat(timesheet.entries[0].duration).isEqualTo(9.hours)
            assertThat(timesheet.entries[0].tags).containsExactly(ImportTimesheet.Entry.Tag("Remote"))
            assertThat(timesheet.entries[1].duration).isEqualTo(9.hours)
            assertThat(timesheet.entries[1].tags).isEmpty()
        }.assertAll()
    }

    @Test
    fun `should throw Exception if no Clockify id is found for given customer`() {
        assertThrows(IllegalArgumentException::class.java) {
            clockifyAdapter.fetchTimesheet(TestFixture.Import.aCustomer.copy(id = Id("xxx")), TestFixture.Import.aDateRange)
        }
    }

    private fun prepareClockifyServer(vararg responseFiles: String) {
        val request = HttpRequest.request()
            .withMethod("POST")
            .withPath("/workspaces/$A_WORKSPACE_ID/reports/detailed")
            .withHeader("X-Api-Key", AN_API_KEY)
            .withHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString())

        responseFiles.forEach {
            mockServer.`when`(request, Times.exactly(1))
                .respond(
                    HttpResponse.response()
                        .withStatusCode(200)
                        .withBody(ResourcesReader.stringFromResourceFile("${this.javaClass.packageName}/$it"))
                        .withDelay(TimeUnit.MILLISECONDS, 500)
                        .withHeaders(
                            Headers(
                                Header.header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
                            )
                        )
                )
        }
    }
}