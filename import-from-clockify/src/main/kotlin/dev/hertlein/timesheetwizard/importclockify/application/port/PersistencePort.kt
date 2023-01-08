package dev.hertlein.timesheetwizard.importclockify.application.port

import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import io.micronaut.core.annotation.Introspected

enum class PersistenceTarget {
    S3
}

@Introspected
data class PersistenceResult(val target: PersistenceTarget, val uri: String)

interface PersistencePort {

    fun save(timesheet: Timesheet): PersistenceResult
}
