package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import java.io.InputStream

internal abstract class DocumentExportStrategy(
    private val repositoryPort: RepositoryPort
) : ExportStrategy {

    abstract fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)

    override fun export(exportParams: Map<String, String>, timesheet: ExportTimesheet) {
        repositoryPort.save(create(exportParams, timesheet))
    }
}