package dev.hertlein.timesheetwizard.core.shared.configloader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.ExportStrategyConfig
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}

@Component
internal class ConfigLoaderCloudAdapter(
    private val cloudPersistence: CloudPersistence,
    private val objectMapper: ObjectMapper
) : CustomerConfigLoader, ClockifyIdsLoader, ExportConfigLoader {

    private val configuration by lazy { loadConfiguration() }

    @Cacheable("customers")
    override fun loadCustomers(): List<Customer> {
        return configuration
            .map { Customer.of(it.id, it.name, it.enabled) }
            .also { logger.info { "Loaded ${it.size} customer(s)." } }
    }

    @Cacheable("clockify-ids")
    override fun loadClockifyIds(): List<ClockifyId> {
        return configuration
            .map { ClockifyId(it.id, it.clockifyId) }
            .also { logger.info { "Loaded ${it.size} clockify Id(s)." } }
    }

    @Cacheable("export-config")
    override fun loadExportConfig(customer: Customer): List<ExportStrategyConfig> {
        return configuration
            .filter { it.id == customer.id.value }
            .flatMap { it.strategies }
            .map { ExportStrategyConfig(it.type, it.params.orEmpty()) }
            .also { logger.info { "Loaded ${it.size} export config(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = cloudPersistence.download("config/configuration.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val id: String,
        val name: String,
        val enabled: Boolean,
        val clockifyId: String,
        val strategies: List<ExportStrategyConfigDto>
    ) {
        data class ExportStrategyConfigDto(val type: String, val params: Map<String, String>?)
    }
}