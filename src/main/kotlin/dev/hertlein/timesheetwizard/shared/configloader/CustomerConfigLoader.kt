package dev.hertlein.timesheetwizard.shared.configloader

import dev.hertlein.timesheetwizard.shared.model.Customer

interface CustomerConfigLoader {

    fun loadCustomers(): List<Customer>
}