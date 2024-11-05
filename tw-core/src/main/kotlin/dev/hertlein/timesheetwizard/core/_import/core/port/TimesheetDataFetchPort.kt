package dev.hertlein.timesheetwizard.core._import.core.port

import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import java.time.LocalDate

internal interface TimesheetDataFetchPort {

    fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet?
}
