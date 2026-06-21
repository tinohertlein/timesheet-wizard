package dev.hertlein.timesheetwizard.core.exporting.adapter.outgoing.repository

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument

internal class FilePathFactory {

    fun filePathFrom(timesheetDocument: TimesheetDocument): String =
        "timesheets/${timesheetDocument.customerName}/${timesheetDocument.exportType.type}/${timesheetDocument.exportType.version}/${timesheetDocument.fileName}"
}
