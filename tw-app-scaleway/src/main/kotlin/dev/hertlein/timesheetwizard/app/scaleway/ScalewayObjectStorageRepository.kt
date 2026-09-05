package dev.hertlein.timesheetwizard.app.scaleway

import dev.hertlein.timesheetwizard.spi.Repository
import io.minio.GetObjectArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Primary


private val logger = KotlinLogging.logger {}

@org.springframework.stereotype.Repository
@Primary
class ScalewayObjectStorageRepository(
    private val minioClient: MinioClient,
    @Value("\${timesheet-wizard.scaleway.storage.bucket}")
    private val bucket: String,
) : Repository {

    override fun type(): String = "ScalewayObjectStorage"

    override fun root(): String = bucket

    override fun download(key: String): ByteArray {
        return minioClient.getObject(
            GetObjectArgs
                .builder()
                .bucket(bucket)
                .`object`(key)
                .build()
        ).readAllBytes()
            .also {
                logger.info { "Downloaded content from ${location(key)} " }
            }
    }

    override fun upload(key: String, content: ByteArray) {
        minioClient.putObject(
            PutObjectArgs.builder()
                .bucket(bucket)
                .`object`(key)
                .data(content, content.size)
                .build()
        ).also {
            logger.info { "Uploaded content to ${location(key)} " }
        }
    }
}