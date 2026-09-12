package dev.hertlein.timesheetwizard.app.azure.util

import com.azure.storage.blob.BlobServiceClientBuilder
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Primary
import io.micronaut.context.annotation.Value
import io.micronaut.objectstorage.azure.AzureBlobStorageConfiguration
import jakarta.inject.Singleton
import org.testcontainers.azure.AzuriteContainer
import org.testcontainers.utility.DockerImageName

@Factory
class TestcontainersConfiguration {

    @Singleton
    fun azureContainer(): AzuriteContainer {
        // Workaround for https://github.com/Azure/Azurite/issues/2623
        return object : AzuriteContainer(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:3.35.0")) {
            override fun configure() {
                super.configure()
                commandParts = commandParts
                    .toMutableList()
                    .apply { add("--skipApiVersionCheck") }
                    .toTypedArray()
            }
        }.also { it.start() }
    }

    @Singleton
    @Primary
    fun blobServiceClientBuilder(azuriteContainer: AzuriteContainer): BlobServiceClientBuilder {
        return BlobServiceClientBuilder().connectionString(azuriteContainer.connectionString)
    }

    @Singleton
    @Primary
    fun blobStorageConfiguration(@Value($$"${micronaut.object-storage.azure.primary.container}") containerName: String): AzureBlobStorageConfiguration {
        return AzureBlobStorageConfiguration("testcontainers").apply { container = containerName }
    }
}
