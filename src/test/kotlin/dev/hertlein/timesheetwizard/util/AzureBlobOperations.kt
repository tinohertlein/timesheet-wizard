package dev.hertlein.timesheetwizard.util

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerClient

object AzureBlobOperations {

    fun upload(client: BlobContainerClient, key: String, content: ByteArray) {
        client.getBlobClient(key).upload(BinaryData.fromBytes(content), true)
    }

    fun download(client: BlobContainerClient, key: String): ByteArray {
        return client.getBlobClient(key).downloadContent().toBytes()
    }
}