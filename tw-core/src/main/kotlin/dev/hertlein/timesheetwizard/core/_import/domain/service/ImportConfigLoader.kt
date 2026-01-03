package dev.hertlein.timesheetwizard.core._import.domain.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core._import.domain.model.Customer
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class ImportConfigLoader(
    private val repository: Repository,
    private val objectMapper: ObjectMapper
) {

    private val configuration by lazy { loadConfiguration() }

    fun loadCustomers(): List<Customer> {
        return configuration
            .map { Customer.of(it.customerId, it.customerName, it.enabled) }
            .also { logger.info { "Loaded ${it.size} customer(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = repository.download("config/import.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val customerName: String,
        val enabled: Boolean,
    )
}