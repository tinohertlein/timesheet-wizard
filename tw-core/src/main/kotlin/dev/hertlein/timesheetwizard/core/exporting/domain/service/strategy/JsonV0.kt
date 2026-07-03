package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportType
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.exporting.domain.port.RepositoryPort
import tools.jackson.databind.json.JsonMapper

/**
 * A special strategy that does a simple JSON export of all the timesheet entries. No customer-related transformations or formatting is applied here.
 */
internal class JsonV0(repositoryPort: RepositoryPort, private val objectMapper: JsonMapper) : DocumentExportStrategy(repositoryPort) {

    override fun type(): ExportType {
        return ExportType.JSON_V0
    }

    override fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument {
        val json = objectMapper.writeValueAsString(timesheet.entries)
        return TimesheetDocument(
            type(),
            fileNameFrom(timesheet.dateRange),
            timesheet.customer.name,
            timesheet.dateRange,
            json.toByteArray()
        )
    }
}