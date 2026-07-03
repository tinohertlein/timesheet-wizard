package dev.hertlein.timesheetwizard.core.importing.domain.service

import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer

internal class CustomerFactory(private val importConfigLoader: ImportConfigLoader) {

    fun customersFrom(customerIds: List<String>): List<Customer> = filterCustomers(customerIds)

    private fun filterCustomers(customerIds: List<String>): List<Customer> {
        return if (isCustomerIdNotApplicable(customerIds)) {
            listOf(Customer.NOT_APPLICABLE)
        } else {
            enabledCustomers()
                .filter {
                    customerIds
                        .isEmpty()
                        .or(it.id.value in customerIds)
                }
        }
    }

    private fun isCustomerIdNotApplicable(customerIds: List<String>): Boolean =
        customerIds.size == 1 && customerIds.first() == Customer.NOT_APPLICABLE.id.value

    private fun enabledCustomers() = importConfigLoader.loadCustomers().filter { it.enabled == true }
}
