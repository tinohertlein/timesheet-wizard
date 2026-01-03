package dev.hertlein.timesheetwizard.core.importing.domain.port

import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet

internal interface ImportingFinishedEventPort {

    fun publish(timesheet: ImportTimesheet)
}