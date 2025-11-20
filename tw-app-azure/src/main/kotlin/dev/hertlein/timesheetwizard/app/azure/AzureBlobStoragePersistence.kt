package dev.hertlein.timesheetwizard.app.azure

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerClient
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import mu.KotlinLogging
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

private val logger = KotlinLogging.logger {}

@Repository
@Primary
class AzureBlobStoragePersistence(
    private val client: BlobContainerClient
) : CloudPersistence {

    override fun type(): String = "AzureBlobStorage"

    override fun root(): String {
        return client.blobContainerName
    }

    override fun download(key: String): ByteArray {
        return client.getBlobClient(key)
            .downloadContent()
            .toBytes()
            .also {
                logger.info { "Downloaded content from ${location(key)} " }
            }
    }

    override fun upload(key: String, content: ByteArray) {
        client.getBlobClient(key)
            .upload(BinaryData.fromBytes(content), true)
            .also {
                logger.info { "Uploaded content to ${location(key)} " }
            }
    }

    private fun location(key: String) = "${type()}/${root()}/$key"
}