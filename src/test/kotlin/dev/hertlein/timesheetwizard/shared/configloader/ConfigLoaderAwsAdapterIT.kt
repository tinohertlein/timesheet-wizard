package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.ExportStrategyConfig
import dev.hertlein.timesheetwizard.util.AwsS3Operations
import dev.hertlein.timesheetwizard.util.ResourcesReader
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client

@DisplayName("ConfigLoaderAwsAdapter")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS, "aws")
@Import(TestcontainersConfiguration::class)
@SpringBootTest
internal class ConfigLoaderAwsAdapterIT {

    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private lateinit var bucket: String

    @SpyBean
    private lateinit var s3Client: S3Client

    @Autowired
    private lateinit var configLoader: ConfigLoaderAwsAdapter

    @BeforeEach
    fun setup() {
        AwsS3Operations.createBucket(s3Client, bucket)
        AwsS3Operations.upload(
            s3Client, bucket,
            "config/configuration.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/configuration.json")
        )
        Mockito.reset(s3Client)
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