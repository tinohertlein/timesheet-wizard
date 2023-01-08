package dev.hertlein.timesheetwizard.importclockify.adapter.lambda.component

import dev.hertlein.timesheetwizard.importclockify.application.port.PersistenceResult
import io.micronaut.core.annotation.Introspected

@Introspected
data class ImportResponse(
    val persistenceResults: List<PersistenceResult>,
    val durationInSeconds: Long
)
