package dev.hertlein.timesheetwizard.shared.cloud

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty("timesheet-wizard.azure.enabled")
class AzureBlobStoragePersistence(
    private val client: BlobContainerClient
) : CloudPersistence {

    override fun root(): String {
        return client.blobContainerName
    }

    override fun download(key: String): String {
        return client.getBlobClient(key).downloadContent().toString()
    }

    override fun upload(key: String, content: ByteArray) {
        client.getBlobClient(key).upload(BinaryData.fromBytes(content), true)
    }
}