package dev.hertlein.timesheetwizard.documentsgenerator.spi

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Customer
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.TimesheetDocument
import java.io.InputStream

interface TimesheetDocumentFactory {

    fun canHandle(customer: Customer): Boolean

    fun create(timesheet: Timesheet): TimesheetDocument

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
