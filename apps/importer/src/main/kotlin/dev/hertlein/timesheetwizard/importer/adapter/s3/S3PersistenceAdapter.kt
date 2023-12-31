package dev.hertlein.timesheetwizard.importer.adapter.s3

import dev.hertlein.timesheetwizard.importer.adapter.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.importer.adapter.s3.component.JsonFactory
import dev.hertlein.timesheetwizard.importer.application.port.PersistencePort
import dev.hertlein.timesheetwizard.importer.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.importer.application.port.PersistenceTarget
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import mu.KotlinLogging
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

private val logger = KotlinLogging.logger {}

private const val PREFIX = "json"

@Singleton
class S3PersistenceAdapter(
    @Value("\${aws.s3.bucket}") private val bucket: String,
    private val s3Client: S3Client,
    private val jsonFactory: JsonFactory,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun save(timesheet: Timesheet): PersistenceResult {
        logger.debug { "Persisting timesheet..." }

        val filename = filenameFactory.create(PREFIX, timesheet)
        val content = jsonFactory.create(timesheet).toByteArray(Charsets.UTF_8)

        upload(filename, content)

        return PersistenceResult(PersistenceTarget.S3, filename)
            .also { logger.debug { "Persisted timesheet as '$it'" } }
    }

    private fun upload(key: String, content: ByteArray): PutObjectResponse {
        return s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(content)
        )
    }
}
