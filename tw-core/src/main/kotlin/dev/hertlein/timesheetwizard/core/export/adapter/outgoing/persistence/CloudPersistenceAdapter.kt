package dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence

import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.export.core.port.PersistencePort
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
internal class CloudPersistenceAdapter(
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