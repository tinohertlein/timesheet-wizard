package dev.hertlein.timesheetwizard.generateexcel.model

data class Customer(val customerId: CustomerId, val customerName: CustomerName)

@JvmInline
value class CustomerId(val value: String)

@JvmInline
value class CustomerName(val value: String)
