package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.testcontainers.shaded.com.google.common.net.MediaType
import java.util.concurrent.TimeUnit

abstract class AbstractE2ESteps {

    lateinit var mockServer: ClientAndServer

    fun given(repository: Repository) {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
        val configFileNames = listOf(
            "config/clockify.json" to "e2e/config/clockify.json",
            "config/export.json" to "e2e/config/export.json",
            "config/import.json" to "e2e/config/import.json",
        )
        configFileNames.forEach {
            repository.upload(
                it.first,
                ResourcesReader.bytesFromResourceFile(it.second)
            )
        }
        prepareClockifyServer()
    }
    
    fun then(repository: Repository) {
        val expectedFileNames = listOf(
            "timesheets/PiedPiper/csv/v1/" to "timesheet_20220101-20221231.csv",
            "timesheets/PiedPiper/xlsx/v1/" to "timesheet_20220101-20221231.xlsx",
            "timesheets/PiedPiper/xlsx/v2/" to "timesheet_20220101-20221231.xlsx",
            "timesheets/PiedPiper/xlsx/v3/" to "timesheet_20220101-20221231.xlsx",
            "timesheets/PiedPiper/pdf/v1/" to "timesheet_20220101-20221231.pdf",
            "timesheets/PiedPiper/json/v1/" to "timesheet.json",
        )

        expectedFileNames.forEach {
            val bytes = repository.download("${it.first}${it.second}")
            assertThat(bytes).isNotEmpty
        }
        mockServer.stop()
    }
    
    fun prepareClockifyServer() {
        val requestBody = ResourcesReader.stringFromResourceFile("e2e/clockify_request.json")
        val responseBody = ResourcesReader.stringFromResourceFile("e2e/clockify_response.json")
        val emptyResponseBody = ResourcesReader.stringFromResourceFile("e2e/empty_clockify_response.json")

        mockServer.reset()
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("POST")
                .withPath("/workspaces/a-workspace-id/reports/detailed")
                .withBody(JsonBody.json(requestBody, MatchType.STRICT))
                .withHeader("X-Api-Key", "an-api-key")
                .withHeader(HttpHeaders.ACCEPT, MediaType.JSON_UTF_8.toString()),
            Times.exactly(1)
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(responseBody)
                .withDelay(TimeUnit.SECONDS, 1)
                .withHeaders(
                    Headers(
                        Header.header(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString())
                    )
                )
        )
        mockServer.`when`(
            HttpRequest.request(), Times.exactly(1)
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(emptyResponseBody)
                .withHeaders(
                    Headers(
                        Header.header("content-type", ContentType.APPLICATION_JSON.mimeType)
                    )
                )
        )
    }
}