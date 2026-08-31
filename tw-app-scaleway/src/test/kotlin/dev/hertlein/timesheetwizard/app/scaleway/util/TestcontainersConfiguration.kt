package dev.hertlein.timesheetwizard.app.scaleway.util

import dev.hertlein.timesheetwizard.app.scaleway.util.TestProfiles.TESTCONTAINERS
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.testcontainers.localstack.LocalStackContainer

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean(initMethod = "start", destroyMethod = "stop")
    @Profile(TESTCONTAINERS)
    fun localStackContainer(): LocalStackContainer {
        return LocalStackContainer("localstack/localstack:4.14.0")
            .withServices("s3")
            .withEnv("LOCALSTACK_AUTH_TOKEN", System.getenv("LOCALSTACK_AUTH_TOKEN"))
    }

    @Bean
    @Profile(TESTCONTAINERS)
    fun minioClient(
        localstackContainer: LocalStackContainer,
        @Value("\${timesheet-wizard.scaleway.storage.bucket}")
        bucket: String
    ): MinioClient {
        return MinioClient.builder()
            .endpoint(localstackContainer.endpoint.toString())
            .credentials(localstackContainer.accessKey, localstackContainer.secretKey)
            .build()
            .also {
                it.makeBucket(
                    MakeBucketArgs.builder()
                        .region("eu-west-1")
                        .bucket(bucket)
                        .build()
                )
            }
    }
}
