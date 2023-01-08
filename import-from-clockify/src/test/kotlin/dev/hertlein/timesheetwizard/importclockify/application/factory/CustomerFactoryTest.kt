package dev.hertlein.timesheetwizard.importclockify.application.factory

import dev.hertlein.timesheetwizard.importclockify.application.config.CustomerConfig
import dev.hertlein.timesheetwizard.importclockify.model.Customer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

private const val CUSTOMER_ID_1 = "customer-id-1"
private const val CUSTOMER_NAME_1 = "customer-name-1"
private const val CLOCKIFY_ID_1 = "clockify-id-1"

private const val CUSTOMER_ID_2 = "customer-id-2"
private const val CUSTOMER_NAME_2 = "customer-name-2"
private const val CLOCKIFY_ID_2 = "clockify-id-2"


private const val CUSTOMER_ID_3 = "customer-id-3"
private const val CUSTOMER_NAME_3 = "customer-name-3"
private const val CLOCKIFY_ID_3 = "clockify-id-3"


@DisplayName("CustomerFactory")
internal class CustomerFactoryTest {

    private val customerFactory = CustomerFactory(
        listOf(
            CustomerConfig(CUSTOMER_ID_1, CUSTOMER_NAME_1, CLOCKIFY_ID_1, true),
            CustomerConfig(CUSTOMER_ID_2, CUSTOMER_NAME_2, CLOCKIFY_ID_2, true),
            CustomerConfig(CUSTOMER_ID_3, CUSTOMER_NAME_3, CLOCKIFY_ID_3, false),
        )
    )

    @Nested
    inner class Create {

        @Test
        fun `should return enabled customer if corresponding customerId is given`() {
            val customers = customerFactory.create(listOf(CUSTOMER_ID_1))

            assertThat(customers).containsExactly(Customer.of(CUSTOMER_ID_1, CUSTOMER_NAME_1, CLOCKIFY_ID_1))
        }

        @Test
        fun `should not return disabled customer if corresponding customerId is given`() {
            val customers = customerFactory.create(listOf(CUSTOMER_ID_3))

            assertThat(customers).isEmpty()
        }

        @Test
        fun `should return all enabled customers if no customerId is given`() {
            val customers = customerFactory.create(emptyList())

            assertThat(customers).containsExactlyInAnyOrder(
                Customer.of(CUSTOMER_ID_2, CUSTOMER_NAME_2, CLOCKIFY_ID_2),
                Customer.of(CUSTOMER_ID_1, CUSTOMER_NAME_1, CLOCKIFY_ID_1)
            )
        }
    }
}
