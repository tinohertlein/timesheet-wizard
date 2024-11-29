package dev.hertlein.timesheetwizard.core._import.core.service

import dev.hertlein.timesheetwizard.core._import.core.model.Customer
import org.springframework.stereotype.Component

@Component
internal class CustomerFactory(private val importConfigLoader: ImportConfigLoader) {

    fun customersFrom(customerIds: List<String>): List<Customer> = filterCustomers(customerIds)

    private fun filterCustomers(customerIds: List<String>): List<Customer> =
        enabledCustomers()
            .filter {
                customerIds
                    .isEmpty()
                    .or(it.id.value in customerIds)
            }

    private fun enabledCustomers() = importConfigLoader.loadCustomers().filter { it.enabled == true }
}
