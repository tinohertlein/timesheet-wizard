package dev.hertlein.timesheetwizard.core._import.domain.port

import dev.hertlein.timesheetwizard.core._import.domain.model.ImportTimesheet

internal interface EventPublishPort {

    fun publish(timesheet: ImportTimesheet)
}