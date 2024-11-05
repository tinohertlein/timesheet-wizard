package dev.hertlein.timesheetwizard.core._import.core.port

import dev.hertlein.timesheetwizard.core.shared.model.Timesheet

internal interface EventPublishPort {

    fun publish(timesheet: Timesheet)
}