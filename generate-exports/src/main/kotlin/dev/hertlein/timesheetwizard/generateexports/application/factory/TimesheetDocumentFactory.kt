package dev.hertlein.timesheetwizard.generateexports.application.factory

import dev.hertlein.timesheetwizard.generateexports.model.Timesheet
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument

fun interface TimesheetDocumentFactory : java.util.function.Function<Timesheet, TimesheetDocument> {

    fun template(file: String) = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
