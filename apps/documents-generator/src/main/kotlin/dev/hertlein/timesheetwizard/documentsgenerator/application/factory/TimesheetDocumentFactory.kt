package dev.hertlein.timesheetwizard.documentsgenerator.application.factory

import java.util.function.Function
import dev.hertlein.timesheetwizard.documentsgenerator.model.Timesheet
import dev.hertlein.timesheetwizard.documentsgenerator.model.TimesheetDocument
import java.io.InputStream

fun interface TimesheetDocumentFactory : Function<Timesheet, TimesheetDocument> {

    fun template(file: String): InputStream? = Thread.currentThread().contextClassLoader.getResourceAsStream(file)
}
