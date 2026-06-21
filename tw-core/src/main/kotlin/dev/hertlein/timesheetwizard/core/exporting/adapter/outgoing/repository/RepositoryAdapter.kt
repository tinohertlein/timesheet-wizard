package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class RepositoryAdapter(
    private val repository: Repository,
    private val filePathFactory: FilePathFactory
) : RepositoryPort {

    override fun save(timesheetDocument: TimesheetDocument) {
        logger.debug { "Persisting document of type '${timesheetDocument.exportType}' ..." }

        val filePath = filePathFactory.filePathFrom(timesheetDocument)

        repository.upload(filePath, timesheetDocument.content)
            .also { logger.debug { "Persisted document to '${repository.root()}/$filePath'" } }
    }
}