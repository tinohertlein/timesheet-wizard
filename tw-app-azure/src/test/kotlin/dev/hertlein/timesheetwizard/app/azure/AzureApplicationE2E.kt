package dev.hertlein.timesheetwizard.app.azure

import com.azure.storage.blob.BlobServiceClient
import dev.hertlein.timesheetwizard.app.azure.util.AzureBlobOperations
import dev.hertlein.timesheetwizard.app.azure.util.SpringTestProfiles.TESTCONTAINERS
import dev.hertlein.timesheetwizard.app.azure.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2E
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DisplayName("Azure Application")
@SpringBootTest
@ActiveProfiles(TESTCONTAINERS)
@Import(TestcontainersConfiguration::class)
class AzureApplicationE2E : AbstractApplicationE2E() {

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
        azureFunctionAdapter.import(Optional.of("""{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}"""))
    }
}