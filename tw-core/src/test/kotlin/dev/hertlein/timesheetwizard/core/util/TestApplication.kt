package dev.hertlein.timesheetwizard.core.util

import dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.config.ClockifyConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication(scanBasePackages = ["dev.hertlein.timesheetwizard"])
@EnableConfigurationProperties(ClockifyConfig::class)
@EnableCaching
class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}