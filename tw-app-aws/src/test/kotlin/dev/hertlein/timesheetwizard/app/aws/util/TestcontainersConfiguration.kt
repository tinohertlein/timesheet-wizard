package dev.hertlein.timesheetwizard.app.aws.util

import org.testcontainers.localstack.LocalStackContainer
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

object TestcontainersConfiguration {

    fun localStackContainer(): LocalStackContainer {
        return LocalStackContainer("localstack/localstack").withServices("s3")
    }

    fun s3Client(localstackContainer: LocalStackContainer): S3Client {
        return S3Client.builder()
            .endpointOverride(localstackContainer.endpoint)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(localstackContainer.accessKey, localstackContainer.secretKey)
                )
            )
            .region(Region.of(localstackContainer.region))
            .build()
    }
}
