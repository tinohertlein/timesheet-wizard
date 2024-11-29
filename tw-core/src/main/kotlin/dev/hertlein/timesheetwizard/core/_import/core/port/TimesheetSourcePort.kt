package dev.hertlein.timesheetwizard.core._import.core.port

import dev.hertlein.timesheetwizard.core._import.core.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core._import.core.model.Customer
import java.time.LocalDate

internal interface TimesheetSourcePort {

    fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): ImportTimesheet?
}
