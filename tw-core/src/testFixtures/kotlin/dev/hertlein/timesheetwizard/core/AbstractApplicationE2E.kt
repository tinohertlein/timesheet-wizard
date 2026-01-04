package dev.hertlein.timesheetwizard.core

import org.apache.http.HttpHeaders
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.io.CleanupMode
import org.junit.jupiter.api.io.TempDir
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody
import org.testcontainers.shaded.com.google.common.net.MediaType
import java.io.File
import java.nio.file.Path
import java.util.concurrent.TimeUnit
import kotlin.io.path.absolutePathString

const val MOCK_SERVER_HOST = "http://localhost"
const val MOCK_SERVER_PORT = 1081

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
open class AbstractApplicationE2E {

    private lateinit var mockServer: ClientAndServer

    @TempDir(cleanup = CleanupMode.ALWAYS)
    lateinit var tempDir: Path

    @BeforeAll
    open fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    open fun afterAll() {
        mockServer.stop()
    }

    protected fun executeTest(
        uploadToStorage: (String, ByteArray) -> Unit,
        downloadFromStorage: (String) -> ByteArray,
        run: () -> Unit
    ) {
        val configFileNames = listOf(
            "config/clockify.json" to "e2e/config/clockify.json",
            "config/export.json" to "e2e/config/export.json",
            "config/import.json" to "e2e/config/import.json",
        )
        configFileNames.forEach {
            uploadToStorage(
                it.first,
                ResourcesReader.bytesFromResourceFile(it.second)
            )
        }
        prepareClockifyServer()
        val expectedFilenames = listOf(
            "customers/PiedPiper/csv/v1/" to "timesheet_20220101-20221231.csv",
            "customers/PiedPiper/xlsx/v1/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/xlsx/v2/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/pdf/v1/" to "timesheet_20220101-20221231.pdf",
        )

        run()

        expectedFilenames.forEach {
            val bytes = downloadFromStorage("${it.first}${it.second}")
            storeLocally(it, bytes)
            assertThat(bytes).isNotEmpty
        }
    }

    private fun storeLocally(fileDescriptors: Pair<String, String>, bytes: ByteArray) {
        val tmpFile = File(tempDir.absolutePathString() + "/${System.currentTimeMillis()}_${fileDescriptors.second}")
        tmpFile.writeBytes(bytes)
        println("Stored ${fileDescriptors.first}${fileDescriptors.second} to ${tmpFile.absolutePath}")
    }

    private fun prepareClockifyServer() {
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