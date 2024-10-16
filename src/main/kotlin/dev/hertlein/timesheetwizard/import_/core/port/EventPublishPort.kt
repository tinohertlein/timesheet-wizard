package dev.hertlein.timesheetwizard.import_.core.port

import dev.hertlein.timesheetwizard.shared.model.Timesheet

interface EventPublishPort {

    fun publish(timesheet: Timesheet)
}