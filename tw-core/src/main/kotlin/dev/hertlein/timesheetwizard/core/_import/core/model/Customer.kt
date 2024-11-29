package dev.hertlein.timesheetwizard.core._import.core.model

internal data class Customer(val id: Id, val name: Name, val enabled: Boolean? = null) {

    companion object {
        fun of(id: String, name: String): Customer {
            return Customer(Id(id), Name(name))
        }

        fun of(id: String, name: String, enabled: Boolean): Customer {
            return Customer(Id(id), Name(name), enabled)
        }
    }

    @JvmInline
    internal value class Id(val value: String)

    @JvmInline
    internal value class Name(val value: String)
}
