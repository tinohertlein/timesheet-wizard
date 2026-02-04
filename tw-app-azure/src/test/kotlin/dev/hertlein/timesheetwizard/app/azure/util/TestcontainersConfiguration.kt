package dev.hertlein.timesheetwizard.app.azure.util

import com.azure.storage.blob.BlobServiceClientBuilder
import dev.hertlein.timesheetwizard.app.azure.util.TestProfiles.TESTCONTAINERS
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.testcontainers.azure.AzuriteContainer
import org.testcontainers.utility.DockerImageName

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS)
    fun azureContainer(): AzuriteContainer {
        return AzuriteContainer(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite"))
    }

    @Bean
    @Profile(TESTCONTAINERS)
    fun blobServiceClientBuilder(azuriteContainer: AzuriteContainer): BlobServiceClientBuilder {
        return BlobServiceClientBuilder().connectionString(azuriteContainer.connectionString)
    }
}
