package dev.hertlein.timesheetwizard.app.aws

import com.google.common.eventbus.EventBus
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(AwsClockifyConfig::class)
class TwAwsApplication {

    @Bean
    fun eventBus(repository: AwsS3Repository, clockifyConfig: AwsClockifyConfig) = Core.bootstrap(repository, clockifyConfig)
}


fun main(args: Array<String>) {
    runApplication<TwAwsApplication>(*args)
}