package dev.hertlein.timesheetwizard.core.shared.configloader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
internal class ConfigLoaderCloudAdapter(
    private val cloudPersistence: CloudPersistence,
    private val objectMapper: ObjectMapper
) : CustomerConfigLoader {

    private val configuration by lazy { loadConfiguration() }

    @Cacheable("customers")
    override fun loadCustomers(): List<Customer> {
        return configuration
            .map { Customer.of(it.id, it.name, it.enabled) }
            .also { logger.info { "Loaded ${it.size} customer(s)." } }
    }


    private fun loadConfiguration(): List<ConfigDto> {
        val json = cloudPersistence.download("config/configuration.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val id: String,
        val name: String,
        val enabled: Boolean,
    )
}