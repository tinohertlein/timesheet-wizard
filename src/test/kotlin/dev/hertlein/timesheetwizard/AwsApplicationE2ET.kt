package dev.hertlein.timesheetwizard

import dev.hertlein.timesheetwizard.util.AwsS3Operations
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import software.amazon.awssdk.services.s3.S3Client

@DisplayName("AWS Application")
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS, "aws")
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class AwsApplicationE2ET : AbstractApplicationE2E() {

    @Autowired
    private lateinit var s3Client: S3Client

    @Value("\${timesheet-wizard.export.aws.s3.bucket}")
    private lateinit var bucket: String

    @Test
    fun `should import and export timesheets to AWS S3`() {
        executeTest(this::uploadToS3Bucket, this::downloadFromS3Bucket)
    }

    private fun uploadToS3Bucket(key: String, bytes: ByteArray) {
        AwsS3Operations.createBucket(s3Client, bucket)
        AwsS3Operations.upload(s3Client, bucket, key, bytes)
    }

    private fun downloadFromS3Bucket(key: String): ByteArray {
        return AwsS3Operations.download(s3Client, bucket, key)
    }
}