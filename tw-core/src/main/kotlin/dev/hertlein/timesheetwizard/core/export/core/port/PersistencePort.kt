package dev.hertlein.timesheetwizard.core.export.core.port

import dev.hertlein.timesheetwizard.core.export.core.model.TimesheetDocument

internal interface PersistencePort {

    fun save(timesheetDocument: TimesheetDocument)
}
