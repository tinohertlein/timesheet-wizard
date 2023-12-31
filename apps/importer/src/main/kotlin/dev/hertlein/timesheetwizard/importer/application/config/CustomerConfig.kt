package dev.hertlein.timesheetwizard.importer.application.config

import io.micronaut.context.annotation.ConfigurationInject
import io.micronaut.context.annotation.EachProperty
import io.micronaut.context.annotation.Parameter

@EachProperty("customer")
data class CustomerConfig
@ConfigurationInject constructor(
    @Parameter val id: String,
    val name: String,
    val clockifyId: String,
    val enabled: Boolean
)
