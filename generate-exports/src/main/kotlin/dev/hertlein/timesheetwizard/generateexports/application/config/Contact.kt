package dev.hertlein.timesheetwizard.generateexports.application.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "contact")
interface Contact {

    fun name(): String
    fun email(): String
}
