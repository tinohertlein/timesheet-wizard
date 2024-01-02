package dev.hertlein.timesheetwizard.documentsgenerator.spi

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet.TimesheetDocument

interface TimesheetDocumentFactory {

    fun canHandle(customer: Customer): Boolean

    fun create(contact: ContactDetails, timesheet: Timesheet): TimesheetDocument
}
