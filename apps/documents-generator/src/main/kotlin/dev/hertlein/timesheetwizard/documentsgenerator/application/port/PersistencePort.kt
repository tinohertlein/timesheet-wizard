package dev.hertlein.timesheetwizard.documentsgenerator.application.port

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.TimesheetDocument
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.Timesheet

enum class PersistenceTarget {
    S3
}

data class PersistenceResult(val target: PersistenceTarget, val uri: String)

interface PersistencePort {

    fun findTimesheetByURI(uri: String): Timesheet

    fun save(timesheetDocument: TimesheetDocument): PersistenceResult
}
