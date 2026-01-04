package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class RepositoryAdapter(
    private val repository: Repository,
    private val filenameFactory: FilenameFactory
) : RepositoryPort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.type}' ..." }

        val metaData = DocumentMetaData.from(timesheetDocument.type)
        val filename = filenameFactory.filenameFrom(metaData, timesheetDocument)

        repository.upload(filename, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '${repository.root()}/$filename'" } }
    }
}