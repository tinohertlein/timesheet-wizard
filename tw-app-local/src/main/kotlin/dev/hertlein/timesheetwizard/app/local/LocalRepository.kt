package dev.hertlein.timesheetwizard.app.local

import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import java.io.File
import java.nio.file.Path
import kotlin.io.path.createParentDirectories

private val logger = KotlinLogging.logger {}

class LocalRepository(private val dataLocation: File) : Repository {

    override fun type(): String = "LocalFileSystem"

    override fun root(): String = dataLocation.toPath().normalize().toString()

    override fun download(key: String): ByteArray {
        val file = Path.of(dataLocation.absolutePath, key).toFile()
        require(file.exists()) { "Failed to download content from ${file.absolutePath}" }

        return file
            .readBytes()
            .also { logger.info { "Downloaded content from ${location(key)} " } }
    }

    override fun upload(key: String, content: ByteArray) {
        Path.of(dataLocation.absolutePath, key)
            .createParentDirectories()
            .toFile()
            .writeBytes(content)
            .also { logger.info { "Uploaded content to ${location(key)} " } }
    }

    private fun location(key: String) = "${type()}:${root()}/$key"
}