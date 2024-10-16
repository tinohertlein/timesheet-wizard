package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.util.ResourcesReader
import dev.hertlein.timesheetwizard.util.S3Operations
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

@DisplayName("ConfigLoaderS3Adapter")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS)
@Import(TestcontainersConfiguration::class)
@SpringBootTest
internal class ConfigLoaderS3AdapterIT {

    @Value("\${timesheet-wizard.config.aws.s3.bucket}")
    private lateinit var bucket: String

    @SpyBean
    private lateinit var s3Client: S3Client

    @Autowired
    private lateinit var configLoader: ConfigLoaderS3Adapter

    @Nested
    inner class LoadCustomers {

        @BeforeEach
        fun setup() {
            S3Operations.createBucket(s3Client, bucket)
            S3Operations.upload(
                s3Client, bucket,
                "config/customers.json",
                ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/customers.json")
            )
            Mockito.reset(s3Client)
        }

        @Test
        fun `should load customers from S3 only once`() {
            configLoader.loadCustomers()
            val customers = configLoader.loadCustomers()

            assertThat(customers).containsExactly(Customer.of("1000", "PiedPiper", true))
            Mockito.verify(s3Client, times(1)).getObject(any<GetObjectRequest>())
        }
    }

    @Nested
    inner class LoadClockifyIds {

        @BeforeEach
        fun setup() {
            S3Operations.createBucket(s3Client, bucket)
            S3Operations.upload(
                s3Client, bucket,
                "config/clockify-ids.json",
                ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/clockify-ids.json")
            )
            Mockito.reset(s3Client)
        }

        @Test
        fun `should load clockify ids from S3 only once`() {
            configLoader.loadClockifyIds()
            val clockifyIds = configLoader.loadClockifyIds()

            SoftAssertions().apply {
                assertThat(clockifyIds).containsOnlyKeys("1000")
                assertThat(clockifyIds).containsValue("62dd35202849d633796f5459")
            }.assertAll()
            Mockito.verify(s3Client, times(1)).getObject(any<GetObjectRequest>())
        }
    }

    @Nested
    inner class LoadExportConfig {

        @BeforeEach
        fun setup() {
            S3Operations.createBucket(s3Client, bucket)
            S3Operations.upload(
                s3Client, bucket,
                "config/export.json",
                ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/export.json")
            )
            Mockito.reset(s3Client)
        }

        @Test
        fun `should load customers from S3 only once`() {
            configLoader.loadExportConfig()
            val exportConfig = configLoader.loadExportConfig()

            SoftAssertions().apply {
                assertThat(exportConfig.strategiesByCustomerId).containsOnlyKeys("1000")
                assertThat(exportConfig.strategiesByCustomerId).containsValue(
                    listOf(
                        "CSV_V1",
                        "XLSX_V1",
                        "XLSX_V2",
                        "PDF_V1"
                    )
                )
            }.assertAll()
            Mockito.verify(s3Client, times(1)).getObject(any<GetObjectRequest>())
        }
    }
}