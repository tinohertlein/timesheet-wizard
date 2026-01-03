package dev.hertlein.timesheetwizard.app.azure

import dev.hertlein.timesheetwizard.core._import.domain.service.ImportService
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(AzureClockifyConfig::class)
class TwAzureApplication {

    @Bean
    fun importService(persistence: AzureBlobStoragePersistence, clockifyConfig: AzureClockifyConfig): ImportService {
        return Core.bootstrap(persistence, clockifyConfig)
    }
}

fun main(args: Array<String>) {
    runApplication<TwAzureApplication>(*args)
}