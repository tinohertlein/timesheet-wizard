package dev.hertlein.timesheetwizard.core.shared.configloader

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.TestApplication
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("ConfigLoader")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
internal class ConfigLoaderIT {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var configLoader: ConfigLoaderCloudAdapter

    @BeforeEach
    fun setup() {
        cloudPersistence.upload(
            "config/configuration.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/configuration.json")
        )
    }

    @Nested
    inner class LoadCustomers {

        @Test
        fun `should load customers`() {
            val customers = configLoader.loadCustomers()

            assertThat(customers).containsExactly(Customer.of("1000", "PiedPiper", true))
        }
    }
}