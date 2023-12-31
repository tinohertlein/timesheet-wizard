package dev.hertlein.timesheetwizard.importer.adapter.s3.component

import dev.hertlein.timesheetwizard.importer.application.config.Environments
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Requires
import io.micronaut.context.annotation.Value
import jakarta.inject.Singleton
import mu.KotlinLogging
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

private val logger = KotlinLogging.logger {}

@Factory
class S3ClientBeanFactory {

    @Singleton
    @Requires(missingBeans = [S3Client::class])
    fun s3ClientFallback(): S3Client {
        return object : S3Client {
            override fun close() = Unit

            override fun serviceName(): String = ""

            override fun putObject(request: PutObjectRequest, body: RequestBody): PutObjectResponse {
                logger.warn { "S3 fallback mode is enabled: not persisting anything." }
                return PutObjectResponse.builder().build()
            }
        }
    }

    @Singleton
    @Requires(env = [Environments.LOCAL_TO_AWS])
    fun s3ClientLocalToAws(
        @Value("\${aws.access-key-id}") accessKeyId: String,
        @Value("\${aws.secret-access-key}") secretAccessKey: String,
        @Value("\${aws.s3.region}") bucketRegion: String
    ): S3Client {
        // When not running in AWS, we need to rely on credentials to access S3.
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        val credentialsProvider = StaticCredentialsProvider.create(credentials)
        return S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(bucketRegion))
            .build()
    }

    @Singleton
    @Requires(env = [Environments.AWS])
    fun s3ClientAws(@Value("\${aws.s3.region}") bucketRegion: String): S3Client {
        // When running in AWS, there is no need to rely on credentials to access S3.
        // Instead, policies can be used to grant access to S3 for the Lambda function.
        return S3Client.builder()
            .region(Region.of(bucketRegion))
            .build()
    }
}
