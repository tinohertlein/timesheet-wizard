package dev.hertlein.timesheetwizard.core._import.core.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core._import.core.model.Customer
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
internal class ImportConfigLoader(
    private val cloudPersistence: CloudPersistence,
    private val objectMapper: ObjectMapper
) {

    private val configuration by lazy { loadConfiguration() }

    @Cacheable("customers")
    fun loadCustomers(): List<Customer> {
        return configuration
            .map { Customer.of(it.customerId, it.customerName, it.enabled) }
            .also { logger.info { "Loaded ${it.size} customer(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = cloudPersistence.download("config/import.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val customerName: String,
        val enabled: Boolean,
    )
}