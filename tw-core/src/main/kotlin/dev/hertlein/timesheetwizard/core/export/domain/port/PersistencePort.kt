package dev.hertlein.timesheetwizard.core.export.domain.port

import dev.hertlein.timesheetwizard.core.export.domain.model.TimesheetDocument

internal interface PersistencePort {

    fun save(timesheetDocument: TimesheetDocument)
}
