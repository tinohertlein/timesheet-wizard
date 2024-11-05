package dev.hertlein.timesheetwizard.core.shared

import dev.hertlein.timesheetwizard.core.shared.configloader.CustomerConfigLoader
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import org.springframework.stereotype.Component

@Component
internal class CustomerFactory(private val customerConfigLoader: CustomerConfigLoader) {

    fun customersFrom(customerIds: List<String>): List<Customer> = filterCustomers(customerIds)

    private fun filterCustomers(customerIds: List<String>): List<Customer> =
        enabledCustomers()
            .filter {
                customerIds
                    .isEmpty()
                    .or(it.id.value in customerIds)
            }

    private fun enabledCustomers() = customerConfigLoader.loadCustomers().filter { it.enabled == true }
}
