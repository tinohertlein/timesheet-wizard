package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.app.aws.util.S3Operations
import dev.hertlein.timesheetwizard.app.aws.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2E
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import software.amazon.awssdk.services.s3.S3Client

@DisplayName("AWS Application")
class AwsApplicationE2E : AbstractApplicationE2E() {

    private lateinit var bucket: String
    private lateinit var s3Client: S3Client
    private val localStackContainer = TestcontainersConfiguration.localStackContainer()

    private lateinit var awsLambdaAdapter: AwsLambdaAdapter

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()
        localStackContainer.start()
    }

    @AfterAll
    override fun afterAll() {
        super.afterAll()
        localStackContainer.stop()
    }

    @BeforeEach
    fun beforeEach() {
        s3Client = TestcontainersConfiguration.s3Client(localStackContainer)
        bucket = "tw-sheets"

        awsLambdaAdapter = AwsLambdaAdapter(
            clockifyConfig = AwsClockifyConfig("$MOCK_SERVER_HOST:$MOCK_SERVER_PORT", "an-api-key", "a-workspace-id"),
            repository = AwsS3Repository(s3Client, bucket)
        )
    }

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
        awsLambdaAdapter.handleRequest(
            """{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""".byteInputStream(Charsets.US_ASCII),
            null,
            null
        )
    }
}