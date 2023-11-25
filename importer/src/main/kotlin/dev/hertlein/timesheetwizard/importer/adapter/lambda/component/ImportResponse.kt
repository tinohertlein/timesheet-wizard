package dev.hertlein.timesheetwizard.importer.adapter.lambda.component

import dev.hertlein.timesheetwizard.importer.application.port.PersistenceResult
import io.micronaut.core.annotation.Introspected

@Introspected
data class ImportResponse(
    val persistenceResults: List<PersistenceResult>,
    val durationInSeconds: Long
)
