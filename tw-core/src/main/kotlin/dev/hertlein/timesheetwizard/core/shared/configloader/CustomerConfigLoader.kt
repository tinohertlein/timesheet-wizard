package dev.hertlein.timesheetwizard.core.shared.configloader

import dev.hertlein.timesheetwizard.core.shared.model.Customer

internal interface CustomerConfigLoader {

    fun loadCustomers(): List<Customer>
}