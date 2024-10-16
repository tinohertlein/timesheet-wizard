package dev.hertlein.timesheetwizard

import dev.hertlein.timesheetwizard.import_.adapter.incoming.lambda.LambdaAdapter
import dev.hertlein.timesheetwizard.util.ResourcesReader
import dev.hertlein.timesheetwizard.util.S3Operations
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.MatchType
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.JsonBody.json
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client
import java.io.File
import java.util.concurrent.TimeUnit


private const val MOCK_SERVER_HOST = "http://localhost"
private const val MOCK_SERVER_PORT = 1081
private const val AN_API_KEY = "an-api-key"
private const val A_WORKSPACE_ID = "a-workspace-id"

@DisplayName("Application")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS)
@SpringBootTest(
    properties = [
        "timesheet-wizard.import.clockify.reports-url=$MOCK_SERVER_HOST:$MOCK_SERVER_PORT",
        "timesheet-wizard.import.clockify.api-key=$AN_API_KEY",
        "timesheet-wizard.import.clockify.workspace-id=$A_WORKSPACE_ID"
    ]
)
@Import(TestcontainersConfiguration::class)
class ApplicationE2ET {

    private val saveDownloadedTimesheets: Boolean = false

    @Autowired
    private lateinit var lambdaAdapter: LambdaAdapter

    @Autowired
    private lateinit var s3Client: S3Client

    private lateinit var mockServer: ClientAndServer

    @Value("\${timesheet-wizard.export.aws.s3.bucket}")
    private lateinit var bucket: String

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
    }

    @Test
    fun `should import and export timesheets`() {
        createS3Bucket()
        uploadToS3Bucket("config/customers.json", ResourcesReader.bytesFromResourceFile("e2e/config/customers.json"))
        uploadToS3Bucket("config/clockify-ids.json", ResourcesReader.bytesFromResourceFile("e2e/config/clockify-ids.json"))
        uploadToS3Bucket("config/export.json", ResourcesReader.bytesFromResourceFile("e2e/config/export.json"))
        prepareClockifyServer()
        val expectedFilenames = listOf(
            "customers/PiedPiper/csv/v1/" to "timesheet_20220101-20221231.csv",
            "customers/PiedPiper/xlsx/v1/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/xlsx/v2/" to "timesheet_20220101-20221231.xlsx",
            "customers/PiedPiper/pdf/v1/" to "timesheet_20220101-20221231.pdf",
        )

        lambdaAdapter.accept("{\"customerIds\": [\"1000\"], \"dateRangeType\": \"CUSTOM_YEAR\", \"dateRange\": \"2022\"}")

        expectedFilenames.forEach {
            val bytes = downloadFromS3Bucket("${it.first}${it.second}")
            assertThat(bytes.size).isGreaterThan(0)

            if (saveDownloadedTimesheets) {
                File("${System.currentTimeMillis()}_${it.second}").writeBytes(bytes)
            }
        }
    }

    private fun createS3Bucket() {
        S3Operations.createBucket(s3Client, bucket)
    }

    private fun uploadToS3Bucket(key: String, bytes: ByteArray) {
        S3Operations.upload(s3Client, bucket, key, bytes)
    }

    private fun downloadFromS3Bucket(key: String): ByteArray {
        return S3Operations.download(s3Client, bucket, key)
    }

    private fun prepareClockifyServer() {
        val requestBody = ResourcesReader.stringFromResourceFile("e2e/clockify_request.json")
        val responseBody = ResourcesReader.stringFromResourceFile("e2e/clockify_response.json")
        val emptyResponseBody = ResourcesReader.stringFromResourceFile("e2e/empty_clockify_response.json")

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name())
                .withPath("/workspaces/${A_WORKSPACE_ID}/reports/detailed")
                .withBody(json(requestBody, MatchType.STRICT))
                .withHeader("X-Api-Key", AN_API_KEY)
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