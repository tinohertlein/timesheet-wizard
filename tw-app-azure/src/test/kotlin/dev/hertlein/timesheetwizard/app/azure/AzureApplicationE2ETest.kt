package dev.hertlein.timesheetwizard.app.azure

import com.azure.storage.blob.BlobContainerClient
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.HttpStatusType
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import io.micronaut.core.annotation.NonNull
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.util.Optional

@DisplayName("Azure Application")
@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AzureApplicationE2ETest : AbstractApplicationE2ETest(), TestPropertyProvider {

    override fun getProperties(): @NonNull Map<String, String> {

        return mapOf(
            "timesheet-wizard.import.clockify.reports-url" to "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT",
            "timesheet-wizard.import.clockify.api-key" to "an-api-key",
            "timesheet-wizard.import.clockify.workspace-id" to "a-workspace-id"
        )
    }

    @Inject
    private lateinit var containerClient: BlobContainerClient

    @Inject
    private lateinit var adapter: AzureFunctionAdapter

    @Inject
    private lateinit var repository: AzureBlobStorageRepository

    @Test
    fun `should import and export timesheets to Azure Blob Storage`() {
        containerClient.createIfNotExists()
        executeTest(repository, this::run)
    }

    private fun run() {
        val input = Optional.of("""{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""")
        val message = object : HttpRequestMessage<Optional<String>> {
            override fun getUri(): URI {
                TODO("Not yet implemented")
            }

            override fun getHttpMethod(): HttpMethod {
                TODO("Not yet implemented")
            }

            override fun getHeaders(): MutableMap<String, String> {
                TODO("Not yet implemented")
            }

            override fun getQueryParameters(): MutableMap<String, String> {
                TODO("Not yet implemented")
            }

            override fun getBody(): Optional<String> {
                return input
            }

            override fun createResponseBuilder(p0: HttpStatus?): HttpResponseMessage.Builder {
                TODO("Not yet implemented")
            }

            override fun createResponseBuilder(p0: HttpStatusType?): HttpResponseMessage.Builder {
                TODO("Not yet implemented")
            }
        }

        adapter.import(message, null)
    }


}