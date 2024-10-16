package dev.hertlein.timesheetwizard.shared.configloader

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.ExportConfig
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest

private val logger = KotlinLogging.logger {}

@Component
class ConfigLoaderS3Adapter(
    private val s3Client: S3Client,
    @Value("\${timesheet-wizard.config.aws.s3.bucket}")
    private val bucket: String,
    private val objectMapper: ObjectMapper
) : CustomerConfigLoader, ClockifyIdsLoader, ExportConfigLoader {

    @Cacheable("customers")
    override fun loadCustomers(): List<Customer> {
        return parseCustomers(download("config/customers.json")).also {
            logger.info { "Loaded ${it.size} customers from S3." }
        }
    }

    @Cacheable("clockify-ids")
    override fun loadClockifyIds(): Map<String, String> {
        return parseClockifyIds(download("config/clockify-ids.json")).also {
            logger.info { "Loaded ${it.size} clockifyIds from S3." }
        }
    }

    @Cacheable("export-config")
    override fun loadExportConfig(): ExportConfig {
        return parseExportConfig(download("config/export.json")).also {
            logger.info { "Loaded exportConfig from S3." }
        }
    }

    private fun download(key: String): String {
        val request = GetObjectRequest
            .builder()
            .bucket(bucket)
            .key(key)
            .build()
        return String(s3Client.getObject(request).readAllBytes())
    }

    private fun parseCustomers(json: String): List<Customer> {
        val parsed = objectMapper.readValue(json, object : TypeReference<List<CustomerDto>?>() {}) ?: emptyList()
        return parsed.map {
            Customer.of(it.id, it.name, it.enabled)
        }
    }

    private fun parseClockifyIds(json: String): Map<String, String> {
        return objectMapper.readValue(json, object : TypeReference<HashMap<String, String>?>() {}) ?: emptyMap()
    }

    private fun parseExportConfig(json: String): ExportConfig {
        return objectMapper.readValue(json, object : TypeReference<ExportConfig>() {})

    }
}

data class CustomerDto(val id: String, val name: String, val enabled: Boolean)