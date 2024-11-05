package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.app.aws.util.S3Operations
import dev.hertlein.timesheetwizard.app.aws.util.SpringTestProfiles.TESTCONTAINERS
import dev.hertlein.timesheetwizard.app.aws.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2E
import dev.hertlein.timesheetwizard.core.TwApplication
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client

@DisplayName("AWS Application")
@SpringBootTest(classes = [TwApplication::class])
@ActiveProfiles(TESTCONTAINERS)
@Import(TestcontainersConfiguration::class)
class AwsApplicationE2E : AbstractApplicationE2E() {

    @Autowired
    private lateinit var lambdaAdapter: LambdaAdapter

    @Autowired
    private lateinit var s3Client: S3Client

    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private lateinit var bucket: String

    @Test
    fun `should import and export timesheets to S3`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        S3Operations.createBucket(s3Client, bucket)
        S3Operations.upload(s3Client, bucket, key, bytes)
    }

    private fun download(key: String): ByteArray {
        return S3Operations.download(s3Client, bucket, key)
    }

    private fun run() {
        lambdaAdapter.accept("""{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""")
    }
}