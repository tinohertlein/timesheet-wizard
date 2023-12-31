package dev.hertlein.timesheetwizard.importer.util

import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Factory
internal class TestConfigurations {

    @Singleton
    @Requires(env = [TestEnvironments.TEST_CONTAINERS])
    fun s3ClientTestContainers(
        @Value("\${aws.s3.bucket}") bucket: String
    ): S3Client {
        val container = TestContainers.S3.container()
        container.start()

        val credentials = AwsBasicCredentials.create(
            TestContainers.S3.ACCESS_KEY_ID,
            TestContainers.S3.SECRET_ACCESS_KEY
        )
        val credentialsProvider = StaticCredentialsProvider.create(credentials)
        val s3Client = S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .endpointOverride(URI.create("http://${container.host}:${container.firstMappedPort}"))
            .forcePathStyle(true)
            .build()

        TestContainers.S3.createBucket(s3Client, bucket)
        return s3Client
    }
}
