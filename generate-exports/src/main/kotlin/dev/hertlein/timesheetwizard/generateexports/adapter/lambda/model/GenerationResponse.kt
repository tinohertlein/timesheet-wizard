package dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model

import dev.hertlein.timesheetwizard.generateexports.application.port.PersistenceResult
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class GenerationResponse(
    val persistenceResults: List<PersistenceResult>,
    val durationInSeconds: Long
)
