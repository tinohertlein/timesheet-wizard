package dev.hertlein.timesheetwizard.shared.cloud

import com.azure.storage.blob.BlobContainerClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


const val CONFIG_QUALIFIER = "blobConfigContainerClient"
const val EXPORT_QUALIFIER = "blobExportContainerClient"

@Configuration
class AzureBeansFactory {

    @Bean
    fun blobServiceClient(blobServiceClientBuilder: BlobServiceClientBuilder): BlobServiceClient {
        return blobServiceClientBuilder.buildClient()
    }

    @Bean(name = [CONFIG_QUALIFIER])
    @ConditionalOnProperty("timesheet-wizard.azure.enabled")
    fun blobConfigContainerClient(
        serviceClient: BlobServiceClient,
        @Value("\${timesheet-wizard.config.azure.blob.container}")
        container: String
    ): BlobContainerClient {
        return serviceClient.createBlobContainerIfNotExists(container)
    }

    @Bean(name = [EXPORT_QUALIFIER])
    @ConditionalOnProperty("timesheet-wizard.azure.enabled")
    fun blobExportContainerClient(
        serviceClient: BlobServiceClient,
        @Value("\${timesheet-wizard.export.azure.blob.container}")
        container: String
    ): BlobContainerClient {
        return serviceClient.createBlobContainerIfNotExists(container)
    }
}