package dev.hertlein.timesheetwizard.export.core.port

import dev.hertlein.timesheetwizard.export.core.model.TimesheetDocument


interface PersistencePort {

    fun save(timesheetDocument: TimesheetDocument)
}
