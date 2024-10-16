package dev.hertlein.timesheetwizard.import_.core.port

import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import java.time.LocalDate

interface TimesheetDataFetchPort {

    fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet?
}
