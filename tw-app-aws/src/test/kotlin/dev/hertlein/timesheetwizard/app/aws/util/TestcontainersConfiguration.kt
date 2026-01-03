package dev.hertlein.timesheetwizard.app.aws.util

import dev.hertlein.timesheetwizard.app.aws.util.TestProfiles.TESTCONTAINERS
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS)
    fun localStackContainer(): LocalStackContainer {
        return LocalStackContainer(DockerImageName.parse("localstack/localstack:3.4.0"))
    }

    @Bean
    @Profile(TESTCONTAINERS)
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
}
