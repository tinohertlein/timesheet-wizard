package dev.hertlein.timesheetwizard.generateexports.application.factory

import dev.hertlein.timesheetwizard.generateexports.model.Timesheet
import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument

typealias TimesheetDocumentFactory = java.util.function.Function<Timesheet, TimesheetDocument>
