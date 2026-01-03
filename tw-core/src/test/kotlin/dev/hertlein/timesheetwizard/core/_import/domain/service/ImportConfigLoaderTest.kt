package dev.hertlein.timesheetwizard.core._import.domain.service

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core._import.domain.model.Customer
import dev.hertlein.timesheetwizard.core.util.TestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ImportConfigLoader")
internal class ImportConfigLoaderTest {

    private val cloudPersistence = TestFixture.App.cloudPersistenceInMemory
    private val configLoader = ImportConfigLoader(cloudPersistence, TestFixture.App.objectMapper)

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