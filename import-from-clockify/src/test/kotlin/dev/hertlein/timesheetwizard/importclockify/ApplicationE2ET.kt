package dev.hertlein.timesheetwizard.importclockify

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.importclockify.adapter.lambda.component.FunctionRequestHandler
import dev.hertlein.timesheetwizard.importclockify.adapter.lambda.component.ImportResponse
import dev.hertlein.timesheetwizard.importclockify.application.config.DateRangeType
import dev.hertlein.timesheetwizard.importclockify.application.config.ImportConfig
import dev.hertlein.timesheetwizard.importclockify.util.ResourcesReader
import dev.hertlein.timesheetwizard.importclockify.util.TestEnvironments
import io.micronaut.context.annotation.Property
import io.micronaut.context.annotation.Value
import io.micronaut.function.aws.proxy.MockLambdaContext
import io.micronaut.http.HttpHeaders
import io.micronaut.http.HttpMethod
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import jakarta.inject.Inject
import org.apache.http.entity.ContentType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockserver.integration.ClientAndServer
import org.mockserver.matchers.Times
import org.mockserver.model.Header
import org.mockserver.model.Headers
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import org.mockserver.model.StringBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import java.util.concurrent.TimeUnit

private const val MOCK_SERVER_HOST = "http://localhost"
private const val MOCK_SERVER_PORT = 1080
private const val AN_API_KEY = "an-api-key"
private const val A_WORKSPACE_ID = "a-workspace-id"

@DisplayName("Application")
@MicronautTest(environments = [TestEnvironments.TEST_CONTAINERS])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Property(name = "clockify.reports-url", value = "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT")
@Property(name = "clockify.api-key", value = AN_API_KEY)
@Property(name = "clockify.workspace-id", value = A_WORKSPACE_ID)
@Property(name = "customer.1000.name", value = "PiedPiper")
@Property(name = "customer.1000.clockify-id", value = "42")
@Property(name = "customer.1000.enabled", value = "true")
class ApplicationE2ET {

    @Inject
    lateinit var objectMapper: ObjectMapper

    @Inject
    lateinit var handler: FunctionRequestHandler

    @Inject
    private lateinit var s3Client: S3Client

    @Value("\${aws.s3.bucket}")
    private lateinit var bucket: String

    private lateinit var mockServer: ClientAndServer

    @BeforeAll
    fun beforeAll() {
        mockServer = ClientAndServer.startClientAndServer(MOCK_SERVER_PORT)
    }

    @AfterAll
    fun afterAll() {
        mockServer.stop()
        handler.applicationContext.close()
    }

    @Test
    fun `should import timesheet from clockify`() {
        prepareClockifyServer()
        val request = createRequest()

        val response = handler.handleRequest(request, MockLambdaContext())

        verify(response)
    }

    private fun createRequest(): APIGatewayProxyRequestEvent? {
        val importConfig = ImportConfig(listOf("1000"), DateRangeType.CUSTOM_MONTH, "2022-01")
        return APIGatewayProxyRequestEvent().withBody(objectMapper.writeValueAsString(importConfig))
    }

    private fun verify(response: APIGatewayProxyResponseEvent) {
        val importResponse = objectMapper.readValue(response.body, ImportResponse::class.java)
        val importedTimesheet = downloadTimesheetFromS3(importResponse.persistenceResults[0].uri)
        val expectedTimesheet = ResourcesReader.stringFromResourceFile("e2e/expected_timesheet.json")

        assertThat(response.statusCode).isEqualTo(200)
        assertThat(importedTimesheet).isEqualToIgnoringWhitespace(expectedTimesheet)
    }

    private fun prepareClockifyServer() {
        val requestBody = ResourcesReader.stringFromResourceFile("e2e/clockify_request.json")
        val responseBody = ResourcesReader.stringFromResourceFile("e2e/clockify_response.json")
        val emptyResponseBody = ResourcesReader.stringFromResourceFile("e2e/empty_clockify_response.json")

        mockServer.`when`(
            HttpRequest.request()
                .withMethod(HttpMethod.POST.name)
                .withPath("/workspaces/${A_WORKSPACE_ID}/reports/detailed")
                .withBody(StringBody.exact(requestBody))
                .withHeader("X-Api-Key", AN_API_KEY)
                .withHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.mimeType), Times.exactly(1)
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

    private fun downloadTimesheetFromS3(fileLocation: String): String {
        val request = GetObjectRequest.builder()
            .bucket(bucket)
            .key(fileLocation)
            .build()
        val bytes = s3Client.getObject(request).readAllBytes()

        return String(bytes)
    }
}
