package dev.hertlein.timesheetwizard.app.azure

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AzureBeansFactory {

    @Bean
    fun blobServiceClient(blobServiceClientBuilder: BlobServiceClientBuilder): BlobServiceClient {
        return blobServiceClientBuilder.buildClient()
    }

    @Bean
    fun blobConfigContainerClient(
        blobServiceClient: BlobServiceClient,
        @Value("\${timesheet-wizard.azure.blob.container}")
        container: String
    ): BlobContainerClient {
        return blobServiceClient.createBlobContainerIfNotExists(container)
    }
}