package dev.hertlein.timesheetwizard.import_.core

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

const val TIMEZONE = "Europe/Berlin"

@Configuration
class ClockFactory {

    @Bean
    fun clock(): Clock = Clock.system(ZoneId.of(TIMEZONE))

}