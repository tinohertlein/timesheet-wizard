package dev.hertlein.timesheetwizard.shared.configloader

import com.azure.storage.blob.BlobServiceClient
import dev.hertlein.timesheetwizard.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.ExportStrategyConfig
import dev.hertlein.timesheetwizard.util.AzureBlobOperations
import dev.hertlein.timesheetwizard.util.ResourcesReader
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@DisplayName("ConfigLoaderAzureAdapter")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS, "azure")
@Import(TestcontainersConfiguration::class)
@SpringBootTest
internal class ConfigLoaderAzureAdapterIT {

    @Autowired
    private lateinit var blobServiceClient: BlobServiceClient

    @Value("\${timesheet-wizard.azure.blob.container}")
    private lateinit var container: String

    @Autowired
    private lateinit var configLoader: ConfigLoaderAzureAdapter

    @BeforeEach
    fun setup() {
        AzureBlobOperations.upload(
            blobServiceClient,
            container,
            "config/configuration.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/configuration.json")
        )
    }

    @Nested
    inner class LoadCustomers {

        @Test
        fun `should load customers`() {
            val customers = configLoader.loadCustomers()

            assertThat(customers).containsExactly(Customer.of("1000", "PiedPiper", true))
        }
    }

    @Nested
    inner class LoadClockifyIds {

        @Test
        fun `should load clockify ids`() {
            val clockifyIds = configLoader.loadClockifyIds()

            assertThat(clockifyIds).containsExactly(ClockifyId("1000", "62dd35202849d633796f5459"))
        }
    }

    @Nested
    inner class LoadExportConfig {

        @Test
        fun `should load export config`() {
            val customer = Customer.of("1000", "PiedPiper", true)
            val exportConfig = configLoader.loadExportConfig(customer)

            assertThat(exportConfig).containsExactly(
                ExportStrategyConfig("CSV_V1", mapOf("login" to "rihe")),
                ExportStrategyConfig(
                    "XLSX_V1", mapOf(
                        "contact-name" to "Richard Hendricks",
                        "contact-email" to "Richard.Hendricks@example.org"
                    )
                ),
                ExportStrategyConfig(
                    "PDF_V1", mapOf(
                        "contact-name" to "Richard Hendricks",
                        "contact-email" to "Richard.Hendricks@example.org"
                    )
                ),
                ExportStrategyConfig("XLSX_V2", emptyMap()),
            )
        }
    }
}