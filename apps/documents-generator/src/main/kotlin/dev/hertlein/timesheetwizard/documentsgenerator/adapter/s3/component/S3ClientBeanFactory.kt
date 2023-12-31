package dev.hertlein.timesheetwizard.documentsgenerator.adapter.s3.component

import dev.hertlein.timesheetwizard.documentsgenerator.application.config.Environments
import io.quarkus.arc.profile.IfBuildProfile
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.ResponseInputStream
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.http.AbortableInputStream
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.GetObjectResponse
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import software.amazon.awssdk.services.s3.model.S3Object
import java.io.ByteArrayInputStream
import jakarta.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}

class S3ClientBeanFactory {

    @ApplicationScoped
    @IfBuildProfile(Environments.LOCAL)
    fun s3ClientDefault(): S3Client {
        return object : S3Client {

            private val storage = mutableMapOf<String, ByteArray>()

            override fun close() = Unit

            override fun serviceName(): String = ""

            override fun putObject(request: PutObjectRequest, body: RequestBody): PutObjectResponse {
                logger.warn { "S3 fallback mode is enabled, so persisting in memory only." }
                val key = request.key()
                val value = body.contentStreamProvider().newStream().readAllBytes()

                storage[key] = value

                return PutObjectResponse.builder().build()
            }

            override fun getObject(request: GetObjectRequest): ResponseInputStream<GetObjectResponse> {
                val key = request.key()

                val bytes = storage[key]
                val response = GetObjectResponse.builder().build()
                val stream = AbortableInputStream.create(ByteArrayInputStream(bytes))

                return ResponseInputStream(response, stream)
            }

            @Suppress("SpreadOperator")
            override fun listObjectsV2(request: ListObjectsV2Request): ListObjectsV2Response {
                val keys = storage.keys.map { S3Object.builder().key(it).build() }
                return ListObjectsV2Response.builder().contents(*keys.toTypedArray()).build()
            }
        }
    }

    @ApplicationScoped
    @IfBuildProfile(Environments.LOCAL_TO_AWS)
    fun s3ClientLocalToAws(
        @ConfigProperty(name = "aws.access-key-id") accessKeyId: String,
        @ConfigProperty(name = "aws.secret-access-key") secretAccessKey: String,
        @ConfigProperty(name = "aws.s3.region") bucketRegion: String
    ): S3Client {
        // When not running in AWS, we need to rely on credentials to access S3.
        val credentials = AwsBasicCredentials.create(accessKeyId, secretAccessKey)
        val credentialsProvider = StaticCredentialsProvider.create(credentials)
        return S3Client.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(bucketRegion))
            .build()
    }

    @ApplicationScoped
    @IfBuildProfile(Environments.AWS)
    fun s3ClientAws(@ConfigProperty(name = "aws.s3.region") bucketRegion: String): S3Client {
        // When running in AWS, there is no need to rely on credentials to access S3.
        // Instead, policies can be used to grant access to S3 for the Lambda function.
        return S3Client.builder()
            .region(Region.of(bucketRegion))
            .build()
    }
}
