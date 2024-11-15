package dev.hertlein.timesheetwizard.app.azure.util

import com.azure.storage.blob.BlobServiceClientBuilder
import dev.hertlein.timesheetwizard.app.azure.util.SpringTestProfiles.TESTCONTAINERS
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.GenericContainer
import org.testcontainers.utility.DockerImageName

private const val AZURE_CONTAINER_PORT = 10000

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS)
    fun azureContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:3.32.0"))
            .withCommand("azurite-blob", "--blobHost", "0.0.0.0")
            .withExposedPorts(AZURE_CONTAINER_PORT)
    }

    @Bean
    @Profile(TESTCONTAINERS)
    fun blobServiceClientBuilder(
        azureContainer: GenericContainer<*>,
        @Value("\${timesheet-wizard.azure.blob.container}")
        container: String
    ): BlobServiceClientBuilder {
        val blobPort = azureContainer.getMappedPort(AZURE_CONTAINER_PORT)
        return BlobServiceClientBuilder()
            .connectionString("DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:$blobPort/devstoreaccount1;")
    }
}
