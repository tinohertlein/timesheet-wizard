package dev.hertlein.timesheetwizard.documentsgenerator.adapter.lambda.model

import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistenceResult
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class GenerationResponse(
    val persistenceResults: List<PersistenceResult>,
    val durationInSeconds: Long
)
