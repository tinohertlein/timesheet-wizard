package dev.hertlein.timesheetwizard.importer.application.port

import dev.hertlein.timesheetwizard.importer.model.Customer
import dev.hertlein.timesheetwizard.importer.model.Timesheet
import java.time.LocalDate

interface ImportPort {

    fun import(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet
}
