package dev.hertlein.timesheetwizard.generateexcel.application.config

import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "contact")
interface Contact {

    fun name(): String
    fun email(): String
}
