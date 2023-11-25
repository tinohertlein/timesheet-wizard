package dev.hertlein.timesheetwizard.importer.adapter.clockify

import dev.hertlein.timesheetwizard.importer.util.ResourcesReader
import dev.hertlein.timesheetwizard.importer.util.TestMother
import io.micronaut.context.annotation.Property
import io.micronaut.http.HttpHeaders
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.verify.VerificationTimes
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import kotlin.time.DurationUnit.HOURS
import kotlin.time.toDuration


private const val MOCK_SERVER_HOST = "http://localhost"
private const val MOCK_SERVER_PORT = 1080
private const val AN_API_KEY = "an-api-key"
private const val A_WORKSPACE_ID = "a-workspace-id"

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Property(name = "clockify.reports-url", value = "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT")
@Property(name = "clockify.api-key", value = AN_API_KEY)
@Property(name = "clockify.workspace-id", value = A_WORKSPACE_ID)
internal class ClockifyImportAdapterIT {

    private lateinit var mockServer: ClientAndServer

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
    }

    @Inject
    private lateinit var clockifyImportAdapter: ClockifyImportAdapter

    @Test
    fun `should import a timesheet`() {
        val aCustomer = TestMother.aCustomer()
        val startDate = LocalDate.of(2022, 1, 1)
        val endDate = LocalDate.of(2022, 12, 31)

        prepareClockifyServer(
            "first_clockify_response.json",
            "second_clockify_response.json",
            "third_clockify_response.json"
        )

        val timesheet =
            clockifyImportAdapter.import(
                aCustomer, startDate..endDate
            )

        mockServer.verify(HttpRequest.request(), VerificationTimes.exactly(3))
        assertThat(timesheet.customer).isEqualTo(aCustomer)
        assertThat(timesheet.dateRange.start).isEqualTo(startDate)
        assertThat(timesheet.dateRange.endInclusive).isEqualTo(endDate)
        assertThat(timesheet.entries[0].duration).isEqualTo(18.toDuration(HOURS))
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
