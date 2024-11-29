package dev.hertlein.timesheetwizard.core.export.core.service.strategy

import dev.hertlein.timesheetwizard.core.export.core.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument
import java.io.InputStream

internal interface ExportStrategy {

    fun type(): TimesheetDocument.Type

    fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
