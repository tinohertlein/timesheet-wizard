package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerClient
import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.DocumentMetaData
import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.export.core.port.PersistencePort
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty("timesheet-wizard.azure.enabled")
class AzurePersistenceAdapter(
    private val client: BlobContainerClient,
    @Value("\${timesheet-wizard.export.azure.blob.container}")
    private val container: String,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.type}' ..." }

        val metaData = DocumentMetaData.from(timesheetDocument.type)
        val filename = filenameFactory.filenameFrom(metaData, timesheetDocument)

        upload(filename, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '$container/$filename'" } }
    }

    private fun upload(key: String, content: ByteArray) {
        client.getBlobClient(key).upload(BinaryData.fromBytes(content))
    }
}