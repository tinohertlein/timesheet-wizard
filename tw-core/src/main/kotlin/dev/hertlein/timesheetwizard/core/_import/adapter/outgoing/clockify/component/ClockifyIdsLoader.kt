package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.component

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.model.ClockifyId
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

private val logger = KotlinLogging.logger {}
@Component
internal class ClockifyIdsLoader(
    private val cloudPersistence: CloudPersistence,
    private val objectMapper: ObjectMapper
) {

    private val configuration by lazy { loadConfiguration() }

    @Cacheable("clockify-ids")
    fun loadClockifyIds(): List<ClockifyId> {
        return configuration
            .map { ClockifyId(it.customerId, it.clockifyId) }
            .also { logger.info { "Loaded ${it.size} clockify Id(s)." } }
    }

    private fun loadConfiguration(): List<ConfigDto> {
        val json = cloudPersistence.download("config/clockify.json")
        return objectMapper.readValue(json, object : TypeReference<List<ConfigDto>?>() {}) ?: emptyList()
    }

    private data class ConfigDto(
        val customerId: String,
        val clockifyId: String,
    )
}