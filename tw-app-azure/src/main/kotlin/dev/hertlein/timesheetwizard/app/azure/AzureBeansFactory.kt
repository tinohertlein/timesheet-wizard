package dev.hertlein.timesheetwizard.app.azure

import dev.hertlein.timesheetwizard.core.anticorruption.Core
import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton

@Factory
class AzureBeansFactory {

    @Singleton
    fun cloudFunctionJsonMapper() = Core.objectMapper

    @Singleton
    fun eventBus(repository: AzureBlobStorageRepository, clockifyConfig: AzureClockifyConfig) = Core.bootstrap(repository, clockifyConfig)

}