package dev.hertlein.timesheetwizard.core.importing.domain.port

import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
import java.time.LocalDate

internal interface TimesheetSourcePort {

    fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): ImportTimesheet?
}
