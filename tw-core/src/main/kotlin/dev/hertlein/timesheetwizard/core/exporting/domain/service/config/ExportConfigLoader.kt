package dev.hertlein.timesheetwizard.core.exporting.domain.service.config

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper

private val logger = KotlinLogging.logger {}

internal class ExportConfigLoader(
    private val repository: Repository,
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
        val json = repository.download("config/export.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val strategies: List<ExportStrategyConfigDto>
    ) {
        data class ExportStrategyConfigDto(val type: String, val params: Map<String, String>?)
    }
}