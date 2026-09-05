package dev.hertlein.timesheetwizard.app.scaleway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(ScalewayClockifyConfig::class)
class TwAzureApplication 

fun main(args: Array<String>) {
    runApplication<TwAzureApplication>(*args)
}