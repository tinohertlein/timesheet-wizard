package dev.hertlein.timesheetwizard.core.shared.configloader

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.ExportStrategyConfig
import dev.hertlein.timesheetwizard.core.util.TestApplication
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("ConfigLoader")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
internal class ConfigLoaderIT {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var configLoader: ConfigLoaderCloudAdapter

    @BeforeEach
    fun setup() {
        cloudPersistence.upload(
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