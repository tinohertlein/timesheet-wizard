package dev.hertlein.timesheetwizard.shared.configloader

import com.azure.storage.blob.BlobContainerClient
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.shared.model.ClockifyId
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.ExportStrategyConfig
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component


private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty("timesheet-wizard.azure.enabled")
class ConfigLoaderAzureAdapter(
    private val client: BlobContainerClient,
    @Value("\${timesheet-wizard.config.azure.blob.container}")
    private val container: String,
    private val objectMapper: ObjectMapper
) : CustomerConfigLoader, ClockifyIdsLoader, ExportConfigLoader {

    private val configuration by lazy { loadConfiguration() }

    @Cacheable("customers")
    override fun loadCustomers(): List<Customer> {
        return configuration
            .map { Customer.of(it.id, it.name, it.enabled) }
            .also { logger.info { "Loaded ${it.size} customers from S3." } }
    }

    @Cacheable("clockify-ids")
    override fun loadClockifyIds(): List<ClockifyId> {
        return configuration
            .map { ClockifyId(it.id, it.clockifyId) }
            .also { logger.info { "Loaded ${it.size} clockifyIds from S3." } }
    }

    @Cacheable("export-config")
    override fun loadExportConfig(customer: Customer): List<ExportStrategyConfig> {
        return configuration
            .filter { it.id == customer.id.value }
            .flatMap { it.strategies }
            .map { ExportStrategyConfig(it.type, it.params.orEmpty()) }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = download("config/configuration.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private fun download(key: String): String {
        return client.getBlobClient(key).downloadContent().toString()
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