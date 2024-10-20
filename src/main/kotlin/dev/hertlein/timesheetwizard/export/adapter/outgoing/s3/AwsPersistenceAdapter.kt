package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3

import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.DocumentMetaData
import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.export.core.port.PersistencePort
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty("timesheet-wizard.aws.enabled")
class AwsPersistenceAdapter(
    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private val bucket: String,
    private val s3Client: S3Client,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.type}' ..." }

        val metaData = DocumentMetaData.from(timesheetDocument.type)
        val filename = filenameFactory.filenameFrom(metaData, timesheetDocument)

        upload(filename, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '$bucket/$filename'" } }
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