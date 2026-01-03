package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import tools.jackson.core.type.TypeReference
import tools.jackson.databind.ObjectMapper

private val logger = KotlinLogging.logger {}

internal class ClockifyIdsLoader(
    private val repository: Repository,
    private val objectMapper: ObjectMapper
) {

    private val configuration by lazy { loadConfiguration() }

    fun loadClockifyIds(): List<ClockifyId> {
        return configuration
            .map { ClockifyId(it.customerId, it.clockifyId) }
            .also { logger.info { "Loaded ${it.size} clockify Id(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = repository.download("config/clockify.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val clockifyId: String,
    )
}