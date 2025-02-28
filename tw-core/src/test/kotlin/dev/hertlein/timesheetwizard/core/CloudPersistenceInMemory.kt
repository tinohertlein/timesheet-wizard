package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
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

    private val store = mutableMapOf<String, ByteArray>()

    override fun type(): String = "memory"

    override fun root(): String = "_"

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

    private fun location(key: String) = "${type()}/${root()}/$key"
}