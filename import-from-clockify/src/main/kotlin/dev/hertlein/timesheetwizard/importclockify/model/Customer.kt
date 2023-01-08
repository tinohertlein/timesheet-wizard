package dev.hertlein.timesheetwizard.importclockify.model

import io.micronaut.core.annotation.Introspected

@Introspected
data class Customer(val customerId: CustomerId, val customerName: CustomerName, val clockifyId: String) {
    companion object {
        fun of(id: String, name: String, clockifyId: String) = Customer(CustomerId(id), CustomerName(name), clockifyId)
    }
}

@Introspected
data class CustomerId(val value: String)

@Introspected
data class CustomerName(val value: String)
