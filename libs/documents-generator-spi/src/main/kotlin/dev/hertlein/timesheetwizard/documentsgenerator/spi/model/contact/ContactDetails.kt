package dev.hertlein.timesheetwizard.documentsgenerator.spi.model.contact

data class ContactDetails(val name: Name, val email: Email) {

    @JvmInline
    value class Name(val value: String)

    @JvmInline
    value class Email(val value: String)
}
