package dev.hertlein.timesheetwizard.app.aws

import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.domain.service.ImportService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(AwsClockifyConfig::class)
class TwAwsApplication {

    @Bean
    fun cloudFunctionJsonMapper() = Core.objectMapper

    @Bean
    fun importService(repository: AwsS3Repository, clockifyConfig: AwsClockifyConfig): ImportService {
        return Core.bootstrap(repository, clockifyConfig)
    }
}


fun main(args: Array<String>) {
    runApplication<TwAwsApplication>(*args)
}