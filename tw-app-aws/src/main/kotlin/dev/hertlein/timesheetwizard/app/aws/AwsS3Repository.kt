package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

private val logger = KotlinLogging.logger {}

class AwsS3Repository(
    private val s3Client: S3Client,
    private val bucket: String,
) : Repository {

    init {
        require(bucket.isNotBlank())
    }

    companion object {

        fun fromProperties() = PropertiesLoader.properties.let {
            val bucket = it.getProperty("timesheet-wizard.aws.s3.bucket", "")
            require(bucket.isNotBlank())
            val region = it.getProperty("timesheet-wizard.aws.s3.region", "")
            require(region.isNotBlank())
            val s3Client = S3Client.builder().region(Region.of(region)).build()

            AwsS3Repository(s3Client, bucket)
        }
    }

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
}