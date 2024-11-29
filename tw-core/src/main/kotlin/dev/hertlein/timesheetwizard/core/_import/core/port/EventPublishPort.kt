package dev.hertlein.timesheetwizard.core._import.core.port

import dev.hertlein.timesheetwizard.core._import.core.model.ImportTimesheet

internal interface EventPublishPort {

    fun publish(timesheet: ImportTimesheet)
}