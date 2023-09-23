package dev.hertlein.timesheetwizard.generateexports.application.port

import dev.hertlein.timesheetwizard.generateexports.model.TimesheetDocument
import dev.hertlein.timesheetwizard.generateexports.model.Timesheet

enum class PersistenceTarget {
    S3
}

data class PersistenceResult(val target: PersistenceTarget, val uri: String)

interface PersistencePort {

    fun findTimesheetByURI(uri: String): Timesheet

    fun save(timesheetDocument: TimesheetDocument): PersistenceResult
}
