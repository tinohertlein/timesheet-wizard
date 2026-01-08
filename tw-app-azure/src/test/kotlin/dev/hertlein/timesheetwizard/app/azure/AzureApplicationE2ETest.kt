package dev.hertlein.timesheetwizard.app.azure

import com.azure.storage.blob.BlobServiceClient
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.HttpStatusType
import dev.hertlein.timesheetwizard.app.azure.util.AzureBlobOperations
import dev.hertlein.timesheetwizard.app.azure.util.TestProfiles.TESTCONTAINERS
import dev.hertlein.timesheetwizard.app.azure.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import java.net.URI
import java.util.Optional

@DisplayName("Azure Application")
@SpringBootTest
@ActiveProfiles(TESTCONTAINERS)
@Import(TestcontainersConfiguration::class)
class AzureApplicationE2ETest : AbstractApplicationE2ETest() {

    companion object {

        @DynamicPropertySource
        @JvmStatic
        fun clockifyProperties(registry: DynamicPropertyRegistry) {
            registry.add("timesheet-wizard.import.clockify.reports-url", { "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT" })
            registry.add("timesheet-wizard.import.clockify.api-key", { "an-api-key" })
            registry.add("timesheet-wizard.import.clockify.workspace-id", { "a-workspace-id" })
        }
    }

    @Autowired
    private lateinit var azureFunctionAdapter: AzureFunctionAdapter

    @Autowired
    private lateinit var blobClient: BlobServiceClient

    @Value("\${timesheet-wizard.azure.blob.container}")
    private lateinit var container: String

    @Test
    fun `should import and export timesheets to Azure Blob Storage`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        AzureBlobOperations.upload(blobClient, container, key, bytes)
    }

    private fun download(key: String): ByteArray {
        return AzureBlobOperations.download(blobClient, container, key)
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
        azureFunctionAdapter.import(message, null)
    }
}