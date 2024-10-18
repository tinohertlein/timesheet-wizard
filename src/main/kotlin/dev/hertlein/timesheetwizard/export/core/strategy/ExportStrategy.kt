package dev.hertlein.timesheetwizard.export.core.strategy

import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import java.io.InputStream

interface ExportStrategy {

    fun type(): TimesheetDocument.Type

    fun create(exportParams: Map<String, String>, timesheet: Timesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
