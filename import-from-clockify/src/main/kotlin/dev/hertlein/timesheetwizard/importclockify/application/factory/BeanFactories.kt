package dev.hertlein.timesheetwizard.importclockify.application.factory

import io.micronaut.context.annotation.Factory
import jakarta.inject.Singleton
import java.time.Clock
import java.time.ZoneId

object BeanFactories {

    const val TIMEZONE = "Europe/Berlin"

    @Factory
    class ClockFactory {
        @Singleton
        fun clock(): Clock = Clock.system(ZoneId.of(TIMEZONE))
    }
}
