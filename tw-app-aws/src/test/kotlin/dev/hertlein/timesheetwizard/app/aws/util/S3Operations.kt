package dev.hertlein.timesheetwizard.app.aws.util

import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

object S3Operations {

    fun createBucket(s3Client: S3Client, bucket: String) {
        s3Client.createBucket(
            CreateBucketRequest.builder()
                .bucket(bucket)
                .build()
        )
    }

    fun upload(s3Client: S3Client, bucket: String, key: String, content: ByteArray): PutObjectResponse {
        return s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(content)
        )
    }

    fun download(s3Client: S3Client, bucket: String, key: String): ByteArray {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val response = s3Client.getObject(request)
        return response.readAllBytes()
    }
}