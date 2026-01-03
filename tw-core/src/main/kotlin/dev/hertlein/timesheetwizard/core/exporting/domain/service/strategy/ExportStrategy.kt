package dev.hertlein.timesheetwizard.core.exporting.domain.service.strategy

import dev.hertlein.timesheetwizard.core.exporting.domain.model.ExportTimesheet
import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument
import java.io.InputStream

internal interface ExportStrategy {

    fun type(): TimesheetDocument.Type

    fun create(exportParams: Map<String, String>, timesheet: ExportTimesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
