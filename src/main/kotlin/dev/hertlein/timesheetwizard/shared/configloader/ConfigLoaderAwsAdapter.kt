package dev.hertlein.timesheetwizard.shared.configloader

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
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

private val logger = KotlinLogging.logger {}

@Component
@ConditionalOnProperty("timesheet-wizard.aws.enabled")
class ConfigLoaderAwsAdapter(
    private val s3Client: S3Client,
    @Value("\${timesheet-wizard.aws.s3.bucket}")
    private val bucket: String,
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
        val request = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build()
        return String(s3Client.getObject(request).readAllBytes())
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
