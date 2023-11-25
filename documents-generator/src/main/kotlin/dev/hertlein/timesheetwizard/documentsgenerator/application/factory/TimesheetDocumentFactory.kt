package dev.hertlein.timesheetwizard.documentsgenerator.application.factory

import dev.hertlein.timesheetwizard.documentsgenerator.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetDocument

fun interface TimesheetDocumentFactory : java.util.function.Function<Timesheet, TimesheetDocument> {

    fun template(file: String) = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
