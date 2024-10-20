package dev.hertlein.timesheetwizard.util

import com.azure.storage.blob.BlobServiceClientBuilder
import dev.hertlein.timesheetwizard.shared.SpringProfiles.AWS
import dev.hertlein.timesheetwizard.shared.SpringProfiles.AZURE
import dev.hertlein.timesheetwizard.util.SpringTestProfiles.TESTCONTAINERS
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS, AWS)
    fun localStackContainer(): LocalStackContainer {
        return LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4.0"))
    }

    @Bean
    @Profile(TESTCONTAINERS, AWS)
    fun s3Client(localstackContainer: LocalStackContainer): S3Client {
        return S3Client.builder()
            .endpointOverride(localstackContainer.getEndpointOverride(LocalStackContainer.Service.S3))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localstackContainer.accessKey, localstackContainer.secretKey)
                )
            )
            .region(Region.of(localstackContainer.region)).build()
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS, AZURE)
    fun azureContainer(): GenericContainer<*> {
        return GenericContainer(DockerImageName.parse("mcr.microsoft.com/azure-storage/azurite:3.32.0"))
            .withCommand("azurite-blob", "--blobHost", "0.0.0.0")
            .withExposedPorts(10000)
    }

    @Bean
    @Profile(TESTCONTAINERS, AZURE)
    fun blobServiceClientBuilder(
        azureContainer: GenericContainer<*>,
        @Value("\${timesheet-wizard.azure.blob.container}")
        container: String
    ): BlobServiceClientBuilder {
        val blobPort = azureContainer.getMappedPort(10000)
        return BlobServiceClientBuilder()
            .connectionString("DefaultEndpointsProtocol=http;AccountName=devstoreaccount1;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==;BlobEndpoint=http://127.0.0.1:$blobPort/devstoreaccount1;")
    }
}
