package dev.hertlein.timesheetwizard.importer.util

import org.testcontainers.containers.GenericContainer
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest

object TestContainers {

    object S3 {
        const val ACCESS_KEY_ID = "an-access-key"
        const val SECRET_ACCESS_KEY = "a-secret-key"

        fun container(): GenericContainer<*> {
            return GenericContainer("quay.io/minio/minio")
                .withEnv("MINIO_ACCESS_KEY", ACCESS_KEY_ID)
                .withEnv("MINIO_SECRET_KEY", SECRET_ACCESS_KEY)
                .withCommand("server", "/data")
                .withExposedPorts(9000)
        }

        fun createBucket(s3Client: S3Client, bucket: String) {
            s3Client.createBucket(
                CreateBucketRequest
                    .builder()
                    .bucket(bucket)
                    .build()
            )
        }
    }
}
