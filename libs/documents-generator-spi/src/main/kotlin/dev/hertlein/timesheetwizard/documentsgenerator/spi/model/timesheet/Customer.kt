package dev.hertlein.timesheetwizard.documentsgenerator.spi.model.timesheet

data class Customer(val id: Id, val name: Name) {

    @JvmInline
    value class Id(val value: String)

    @JvmInline
    value class Name(val value: String)
}
