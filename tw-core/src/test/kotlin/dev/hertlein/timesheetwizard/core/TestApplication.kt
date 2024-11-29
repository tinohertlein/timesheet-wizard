package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config.ClockifyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard.core"])
@EnableConfigurationProperties(ClockifyConfig::class)
@EnableCaching
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}