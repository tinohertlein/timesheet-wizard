package dev.hertlein.timesheetwizard.generateexcel.adapter.lambda.model

import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistenceResult
import io.quarkus.runtime.annotations.RegisterForReflection

@RegisterForReflection
data class GenerationResponse(
    val persistenceResult: PersistenceResult,
    val durationInSeconds: Long
)
