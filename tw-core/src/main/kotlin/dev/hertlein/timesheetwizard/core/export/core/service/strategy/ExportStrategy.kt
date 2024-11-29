package dev.hertlein.timesheetwizard.core.export.core.strategy

import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import java.io.InputStream

internal interface ExportStrategy {

    fun type(): TimesheetDocument.Type

    fun create(exportParams: Map<String, String>, timesheet: Timesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
