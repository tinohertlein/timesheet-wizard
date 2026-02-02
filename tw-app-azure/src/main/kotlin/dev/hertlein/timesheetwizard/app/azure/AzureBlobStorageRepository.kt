package dev.hertlein.timesheetwizard.app.azure

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerClient
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import mu.KotlinLogging
import org.springframework.context.annotation.Primary

private val logger = KotlinLogging.logger {}

@org.springframework.stereotype.Repository
@Primary
class AzureBlobStorageRepository(
    private val client: BlobContainerClient
) : Repository {

    override fun type(): String = "AzureBlobStorage"

    override fun root(): String = client.blobContainerName

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
}