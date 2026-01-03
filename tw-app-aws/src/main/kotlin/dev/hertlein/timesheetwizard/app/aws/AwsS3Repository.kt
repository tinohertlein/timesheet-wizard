package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

private val logger = KotlinLogging.logger {}

@org.springframework.stereotype.Repository
@Primary
class AwsS3Repository(
    private val s3Client: S3Client,
    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private val bucket: String,
) : Repository {

    override fun type(): String = "S3"

    override fun root(): String = bucket

    override fun download(key: String): ByteArray {
        return s3Client.getObject(
            GetObjectRequest
                .builder()
                .bucket(bucket)
                .key(key)
                .build()
        ).readAllBytes()
            .also {
                logger.info { "Downloaded content from ${location(key)} " }
            }
    }

    override fun upload(key: String, content: ByteArray) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(content)
        ).also {
            logger.info { "Uploaded content to ${location(key)} " }
        }
    }

    private fun location(key: String) = "${type()}/${root()}/$key"
}