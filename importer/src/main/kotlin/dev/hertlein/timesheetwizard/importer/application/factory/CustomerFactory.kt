package dev.hertlein.timesheetwizard.importer.application.factory

import dev.hertlein.timesheetwizard.importer.application.config.CustomerConfig
import dev.hertlein.timesheetwizard.importer.model.Customer
import jakarta.inject.Singleton

@Singleton
class CustomerFactory(private val customerConfigs: List<CustomerConfig>) {

    fun create(requestedCustomerIds: List<String>): List<Customer> =
        filterRequestedCustomers(requestedCustomerIds)
            .map { Customer.of(it.id, it.name, it.clockifyId) }

    private fun filterRequestedCustomers(requestedCustomerIds: List<String>): List<CustomerConfig> =
        enabledCustomers()
            .filter {
                requestedCustomerIds
                    .isEmpty()
                    .or(it.id in requestedCustomerIds)
            }

    private fun enabledCustomers() = customerConfigs.filter { it.enabled }
}
