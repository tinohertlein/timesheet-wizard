package dev.hertlein.timesheetwizard.core.shared

import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

private val logger = KotlinLogging.logger {}

@Configuration
internal class Factory {

    @Bean
    @ConditionalOnMissingBean
    fun cloudPersistenceInMemory(): CloudPersistenceInMemory = CloudPersistenceInMemory()
}

internal class CloudPersistenceInMemory : CloudPersistence {

    @PostConstruct
    fun init() {
        logger.warn { "No other bean found for ${CloudPersistence::class} -> Creating memory store." }
    }

    private val store = mutableMapOf<String, ByteArray>()

    override fun root(): String = "map"

    override fun upload(key: String, content: ByteArray) {
        store.put(key, content).also {
            logger.info { "Uploaded content to ${location(key)} " }
        }
    }

    override fun download(key: String): ByteArray {
        if (!store.containsKey(key)) {
            throw IllegalArgumentException("No content found for key '$key'")
        }
        return store.get(key)!!.also {
            logger.info { "Downloaded content from ${location(key)} " }
        }
    }

    private fun location(key: String) = "/${root()}/$key"
}