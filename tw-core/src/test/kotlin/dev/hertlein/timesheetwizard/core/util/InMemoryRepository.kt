package dev.hertlein.timesheetwizard.core.util

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

class InMemoryRepository : Repository {

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
        return store[key]!!.also {
            logger.info { "Downloaded content from ${location(key)} " }
        }
    }
}