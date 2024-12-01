package dev.hertlein.timesheetwizard.core._import.domain.service

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.ZoneId

const val TIMEZONE = "Europe/Berlin"

@Configuration
internal class ClockFactory {

    @Bean
    fun clock(): Clock = Clock.system(ZoneId.of(TIMEZONE))

}