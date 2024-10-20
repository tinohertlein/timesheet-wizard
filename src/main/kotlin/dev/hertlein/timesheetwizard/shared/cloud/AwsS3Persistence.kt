package dev.hertlein.timesheetwizard.shared.cloud

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@ConditionalOnProperty("timesheet-wizard.aws.enabled")
@Component
class AwsS3Persistence(
    private val s3Client: S3Client,
    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private val bucket: String,
) : CloudPersistence {

    override fun root(): String {
        return bucket
    }

    override fun download(key: String): String {
        val request = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build()
        return String(s3Client.getObject(request).readAllBytes())
    }

    override fun upload(key: String, content: ByteArray) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(content)
        )
    }
}