package dev.hertlein.timesheetwizard.shared

import dev.hertlein.timesheetwizard.shared.configloader.CustomerConfigLoader
import dev.hertlein.timesheetwizard.shared.model.Customer
import org.springframework.stereotype.Component

@Component
class CustomerFactory(private val customerLoaderPort: CustomerConfigLoader) {

    fun customersFrom(customerIds: List<String>): List<Customer> = filterCustomers(customerIds)

    private fun filterCustomers(customerIds: List<String>): List<Customer> =
        enabledCustomers()
            .filter {
                customerIds
                    .isEmpty()
                    .or(it.id.value in customerIds)
            }

    private fun enabledCustomers() = customerLoaderPort.loadCustomers().filter { it.enabled == true }
}
