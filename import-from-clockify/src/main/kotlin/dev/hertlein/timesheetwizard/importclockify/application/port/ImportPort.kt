package dev.hertlein.timesheetwizard.importclockify.application.port

import dev.hertlein.timesheetwizard.importclockify.model.Customer
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import java.time.LocalDate

interface ImportPort {

    fun import(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet
}
