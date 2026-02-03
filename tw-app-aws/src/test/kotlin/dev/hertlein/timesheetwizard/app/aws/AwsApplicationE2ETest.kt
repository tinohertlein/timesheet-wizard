package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.app.aws.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junitpioneer.jupiter.SetEnvironmentVariable
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest

@DisplayName("AWS Application")
@SetEnvironmentVariable(key = "CLOCKIFY_API_KEY", value = "an-api-key")
@SetEnvironmentVariable(key = "CLOCKIFY_WORKSPACE_ID", value = "a-workspace-id")
class AwsApplicationE2ETest : AbstractApplicationE2ETest() {

    private val localStackContainer = TestcontainersConfiguration.localStackContainer()

    private lateinit var adapter: AwsLambdaAdapter
    private lateinit var repository: AwsS3Repository

    @BeforeAll
    override fun beforeAll() {
        super.beforeAll()
        localStackContainer.start()
        
        val s3Client = TestcontainersConfiguration.s3Client(localStackContainer)
        val bucket = "tw-sheets"
        createBucket(s3Client, bucket)
        
        repository = AwsS3Repository(s3Client, bucket)

        adapter = AwsLambdaAdapter(
            clockifyConfig = AwsClockifyConfig.fromEnv("$MOCK_SERVER_HOST:$MOCK_SERVER_PORT"),
            repository = repository
        )
    }

    private fun createBucket(s3Client: S3Client, bucket: String) {
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucket)
                .build()
        )
    }

    @AfterAll
    override fun afterAll() {
        localStackContainer.stop()
        super.afterAll()
    }

    @Test
    fun `should import and export timesheets to AWS S3`() {
        executeTest(repository, this::run)
    }

    private fun run() {
        adapter.handleRequest(
            """{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}""".byteInputStream(Charsets.US_ASCII),
            null,
            null
        )
    }
}