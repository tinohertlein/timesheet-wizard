package dev.hertlein.timesheetwizard.importer.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class Customer(
    val customerId: CustomerId,
    val customerName: CustomerName,
    val clockifyId: ClockifyId
) {
    companion object {
        fun of(id: String, name: String, clockifyId: String) =
            Customer(CustomerId(id), CustomerName(name), ClockifyId(clockifyId))
    }
}

@Introspected
@JvmInline
value class CustomerId(val value: String)

@Introspected
@JvmInline
value class CustomerName(val value: String)

@Introspected
@JvmInline
value class ClockifyId(val value: String)
