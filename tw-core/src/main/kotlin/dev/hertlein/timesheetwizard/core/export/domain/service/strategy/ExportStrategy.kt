package dev.hertlein.timesheetwizard.core.export.domain.service.strategy

import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.domain.model.TimesheetDocument
import java.io.InputStream

internal interface ExportStrategy {

    fun type(): TimesheetDocument.Type

    fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
