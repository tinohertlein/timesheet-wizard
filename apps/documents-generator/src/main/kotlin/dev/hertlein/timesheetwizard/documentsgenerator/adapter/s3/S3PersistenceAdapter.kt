package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3

import dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component.DocumentMetaData
import dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component.JsonMapper
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistencePort
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistenceTarget
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument
import jakarta.enterprise.context.ApplicationScoped
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

private val logger = KotlinLogging.logger {}

@ApplicationScoped
class S3PersistenceAdapter(
    @ConfigProperty(name = "aws.s3.bucket")
    private val bucket: String,
    private val s3Client: S3Client,
    private val jsonMapper: JsonMapper,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun findTimesheetByURI(uri: String): Timesheet {
        val json = download(uri)
        return jsonMapper.toTimesheetEntity(json)
    }

    override fun save(timesheetDocument: TimesheetDocument): PersistenceResult {
        logger.debug { "Persisting Document of type ${timesheetDocument.type}..." }

        val metaData = DocumentMetaData.of(timesheetDocument.type)
        val filename = filenameFactory.create(metaData, timesheetDocument)

        upload(filename, timesheetDocument.content)

        return PersistenceResult(PersistenceTarget.S3, filename)
            .also { logger.debug { "Persisted Document as '$it'" } }
    }

    private fun download(key: String): String {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val response = s3Client.getObject(request)
        return String(response.readAllBytes())
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
