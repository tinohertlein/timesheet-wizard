package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3

import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.DocumentMetaData
import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.export.core.port.PersistencePort
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ResourceLoader
import org.springframework.core.io.WritableResource
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class AzurePersistenceAdapter(
    private val resourceLoader: ResourceLoader,
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
        val resource = resourceLoader.getResource("azure-blob://$container/$key") as WritableResource
        resource.outputStream.use { os ->
            os.write(content)
        }
    }
}