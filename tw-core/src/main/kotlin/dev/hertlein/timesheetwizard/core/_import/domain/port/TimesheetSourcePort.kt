package dev.hertlein.timesheetwizard.core._import.domain.port

import dev.hertlein.timesheetwizard.core._import.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core._import.domain.model.Customer
import java.time.LocalDate

internal interface TimesheetSourcePort {

    fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): ImportTimesheet?
}
