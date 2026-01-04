package dev.hertlein.timesheetwizard.core.exporting.domain.port

import dev.hertlein.timesheetwizard.core.exporting.domain.model.TimesheetDocument

internal interface RepositoryPort {

    fun save(timesheetDocument: TimesheetDocument)
}
