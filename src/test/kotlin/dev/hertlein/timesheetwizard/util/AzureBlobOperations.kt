package dev.hertlein.timesheetwizard.util

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobServiceClient

object AzureBlobOperations {

    fun upload(client: BlobServiceClient, container: String, key: String, content: ByteArray) {
        client
            .createBlobContainerIfNotExists(container)
            .getBlobClient(key)
            .upload(BinaryData.fromBytes(content), true)
    }

    fun download(client: BlobServiceClient, container: String, key: String): ByteArray {
        return client
            .createBlobContainerIfNotExists(container)
            .getBlobClient(key)
            .downloadContent()
            .toBytes()
    }
}