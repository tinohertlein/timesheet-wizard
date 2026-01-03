package dev.hertlein.timesheetwizard.core.importing.domain.service

import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CUSTOMER_ID_1 = "customer-id-1"
private const val CUSTOMER_NAME_1 = "customer-name-1"

private const val CUSTOMER_ID_2 = "customer-id-2"
private const val CUSTOMER_NAME_2 = "customer-name-2"

private const val CUSTOMER_ID_3 = "customer-id-3"
private const val CUSTOMER_NAME_3 = "customer-name-3"


@DisplayName("CustomerFactory")
internal class CustomerFactoryTest {

    private val importConfigLoader: ImportConfigLoader = mockk()

    private val customerFactory = CustomerFactory(importConfigLoader)

    @BeforeEach
    fun setUp() {
        every { importConfigLoader.loadCustomers() } returns listOf(
            Customer(Customer.Id(CUSTOMER_ID_1), Customer.Name(CUSTOMER_NAME_1), true),
            Customer(Customer.Id(CUSTOMER_ID_2), Customer.Name(CUSTOMER_NAME_2), true),
            Customer(Customer.Id(CUSTOMER_ID_3), Customer.Name(CUSTOMER_NAME_3), false)
        )
    }

    @Nested
    inner class CustomersFrom {

        @Test
        fun `should return enabled customer if corresponding customerId is given`() {
            val customers = customerFactory.customersFrom(listOf(CUSTOMER_ID_1))

            Assertions.assertThat(customers).containsExactly(Customer.of(CUSTOMER_ID_1, CUSTOMER_NAME_1, true))
        }

        @Test
        fun `should not return disabled customer if corresponding customerId is given`() {
            val customers = customerFactory.customersFrom(listOf(CUSTOMER_ID_3))

            Assertions.assertThat(customers).isEmpty()
        }

        @Test
        fun `should return all enabled customers if no customerId is given`() {
            val customers = customerFactory.customersFrom(emptyList())

            Assertions.assertThat(customers).containsExactlyInAnyOrder(
                Customer.of(CUSTOMER_ID_2, CUSTOMER_NAME_2, true),
                Customer.of(CUSTOMER_ID_1, CUSTOMER_NAME_1, true)
            )
        }
    }
}