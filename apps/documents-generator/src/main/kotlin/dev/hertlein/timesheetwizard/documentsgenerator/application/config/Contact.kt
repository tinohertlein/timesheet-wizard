package dev.hertlein.timesheetwizard.documentsgenerator.application.config

import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails.Email
import dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact.ContactDetails.Name
import io.smallrye.config.ConfigMapping

@ConfigMapping(prefix = "contact")
interface Contact {

    fun name(): String
    fun email(): String

    fun toContactDetails() = ContactDetails(Name(name()), Email(email()))
}
