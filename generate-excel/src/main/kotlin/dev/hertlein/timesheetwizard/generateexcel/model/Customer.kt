package dev.hertlein.timesheetwizard.generateexcel.model

data class Customer(val customerId: CustomerId, val customerName: CustomerName)

data class CustomerId(val value: String)

data class CustomerName(val value: String)
