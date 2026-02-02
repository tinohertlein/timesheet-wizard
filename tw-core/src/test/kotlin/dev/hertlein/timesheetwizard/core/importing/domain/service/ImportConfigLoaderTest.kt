package dev.hertlein.timesheetwizard.core.importing.domain.service

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.util.TestFixture
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("ImportConfigLoader")
internal class ImportConfigLoaderTest {

    private val repository = TestFixture.App.inMemoryRepository
    private val configLoader = ImportConfigLoader(repository, TestFixture.App.objectMapper)

    @BeforeEach
    fun setup() {
        repository.upload(
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