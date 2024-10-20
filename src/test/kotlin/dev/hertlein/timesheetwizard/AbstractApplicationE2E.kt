package dev.hertlein.timesheetwizard

import dev.hertlein.timesheetwizard.import_.adapter.incoming.lambda.LambdaAdapter
import dev.hertlein.timesheetwizard.util.ResourcesReader
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.Times
import org.mockserver.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.io.File
import java.util.concurrent.TimeUnit


const val MOCK_SERVER_HOST = "http://localhost"
const val MOCK_SERVER_PORT = 1081

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AbstractApplicationE2E {

    companion object {

        @DynamicPropertySource
        @JvmStatic
        fun clockifyProperties(registry: DynamicPropertyRegistry) {
            registry.add("timesheet-wizard.import.clockify.reports-url", { "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT" })
            registry.add("timesheet-wizard.import.clockify.api-key", { "an-api-key" })
            registry.add("timesheet-wizard.import.clockify.workspace-id", { "a-workspace-id" })
        }
    }

    private val saveDownloadedTimesheets: Boolean = false

    @Autowired
    protected lateinit var lambdaAdapter: LambdaAdapter

    private lateinit var mockServer: ClientAndServer

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
    }


    protected fun executeTest(prepareStorage: (String, ByteArray) -> Unit, downloadFromStorage: (String) -> ByteArray) {
        prepareStorage(
            "config/configuration.json",
            ResourcesReader.bytesFromResourceFile("e2e/config/configuration.json")
        )
        prepareClockifyServer()
        val expectedFilenames = listOf(
            "customers/PiedPiper/csv/v1/" to "timesheet_20220101-20221231.csv",
            "customers/PiedPiper/xlsx/v1/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/xlsx/v2/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/pdf/v1/" to "timesheet_20220101-20221231.pdf",
        )

        lambdaAdapter.accept("{\"customerIds\": [\"1000\"], \"dateRangeType\": \"CUSTOM_YEAR\", \"dateRange\": \"2022\"}")

        expectedFilenames.forEach {
            val bytes = downloadFromStorage("${it.first}${it.second}")
            Assertions.assertThat(bytes.size).isGreaterThan(0)

            if (saveDownloadedTimesheets) {
                File("${System.currentTimeMillis()}_${it.second}").writeBytes(bytes)
            }
        }
    }


    private fun prepareClockifyServer() {
        val requestBody = ResourcesReader.stringFromResourceFile("e2e/clockify_request.json")
        val responseBody = ResourcesReader.stringFromResourceFile("e2e/clockify_response.json")
        val emptyResponseBody = ResourcesReader.stringFromResourceFile("e2e/empty_clockify_response.json")

        mockServer.reset()
        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/workspaces/a-workspace-id/reports/detailed")
                .withBody(JsonBody.json(requestBody, MatchType.STRICT))
                .withHeader("X-Api-Key", "an-api-key")
                .withHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.mimeType),
            Times.exactly(1)
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseBody)
                .withDelay(TimeUnit.SECONDS, 1)
                .withHeaders(
                    Headers(
                        Header.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                    )
                )
        )

        mockServer.`when`(
            HttpRequest.request(), Times.exactly(1)
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(emptyResponseBody)
                .withDelay(TimeUnit.SECONDS, 1)
                .withHeaders(
                    Headers(
                        Header.header(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.mimeType)
                    )
                )
        )
    }
}