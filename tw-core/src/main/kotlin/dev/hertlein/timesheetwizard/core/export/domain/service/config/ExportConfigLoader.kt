package dev.hertlein.timesheetwizard.core.export.domain.service.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

internal class ExportConfigLoader(
    private val cloudPersistence: CloudPersistence,
    private val objectMapper: ObjectMapper
) {

    private val configuration by lazy { loadConfiguration() }

    fun loadExportConfig(customerId: String): List<ExportConfig> {
        return configuration
            .filter { it.customerId == customerId }
            .flatMap { it.strategies }
            .map { ExportConfig(it.type, it.params.orEmpty()) }
            .also { logger.info { "Loaded ${it.size} export config(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = cloudPersistence.download("config/export.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val strategies: List<ExportStrategyConfigDto>
    ) {
        data class ExportStrategyConfigDto(val type: String, val params: Map<String, String>?)
    }
}