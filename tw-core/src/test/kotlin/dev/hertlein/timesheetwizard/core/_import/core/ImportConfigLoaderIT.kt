package dev.hertlein.timesheetwizard.core._import.core

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.TestApplication
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@DisplayName("ImportConfigLoader")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = [TestApplication::class])
internal class ImportConfigLoaderIT {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var configLoader: ImportConfigLoader

    @BeforeEach
    fun setup() {
        cloudPersistence.upload(
            "config/import.json",
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/import.json")
        )
    }

    @Test
    fun `should load customers`() {
        val customers = configLoader.loadCustomers()

        assertThat(customers).containsExactly(Customer.of("1000", "PiedPiper", true))
    }
}