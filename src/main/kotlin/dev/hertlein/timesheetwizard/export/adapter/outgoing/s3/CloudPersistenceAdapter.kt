package dev.hertlein.timesheetwizard.export.adapter.outgoing.s3

import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.DocumentMetaData
import dev.hertlein.timesheetwizard.export.adapter.outgoing.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.export.core.port.PersistencePort
import dev.hertlein.timesheetwizard.shared.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
class CloudPersistenceAdapter(
    private val cloudPersistence: CloudPersistence,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.type}' ..." }

        val metaData = DocumentMetaData.from(timesheetDocument.type)
        val filename = filenameFactory.filenameFrom(metaData, timesheetDocument)

        cloudPersistence.upload(filename, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '${cloudPersistence.root()}/$filename'" } }
    }
}