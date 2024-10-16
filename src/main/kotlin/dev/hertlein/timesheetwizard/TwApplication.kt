package dev.hertlein.timesheetwizard

import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.config.ClockifyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableConfigurationProperties(ClockifyConfig::class)
@EnableCaching
class TwApplication

fun main(args: Array<String>) {
    runApplication<TwApplication>(*args)
}