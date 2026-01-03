package dev.hertlein.timesheetwizard.core.export.adapter.outgoing.persistence

import dev.hertlein.timesheetwizard.core.export.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.export.domain.port.PersistencePort
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class CloudPersistenceAdapter(
    private val repository: Repository,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.type}' ..." }

        val metaData = DocumentMetaData.from(timesheetDocument.type)
        val filename = filenameFactory.filenameFrom(metaData, timesheetDocument)

        repository.upload(filename, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '${repository.root()}/$filename'" } }
    }
}