package dev.hertlein.timesheetwizard.documentsgenerator

import dev.hertlein.timesheetwizard.documentsgenerator.adapter.lambda.model.S3Entity
import dev.hertlein.timesheetwizard.documentsgenerator.adapter.lambda.model.S3EventNotification
import dev.hertlein.timesheetwizard.documentsgenerator.adapter.lambda.model.S3EventNotificationRecord
import dev.hertlein.timesheetwizard.documentsgenerator.adapter.lambda.model.S3ObjectEntity
import dev.hertlein.timesheetwizard.documentsgenerator.util.ResourcesReader
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import jakarta.inject.Inject
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse

@QuarkusTest
@DisplayName("Application")
internal class ApplicationE2ET {

    @Inject
    lateinit var s3Client: S3Client

    @ConfigProperty(name = "aws.s3.bucket")
    lateinit var bucket: String

    @Test
    fun `should generate an xlsx file, a pdf file and a csv file from json`() {
        createBucket()
        val jsonFilename = "PiedPiper_2022-01-01_2022-12-31_e47dc4a0-2899-41a7-a390-a5c6152f2e42.json"
        val jsonFileContent = readResource(jsonFilename)
        val expectedFilenames = listOf(
            "timesheet_PiedPiper_20220101-20221231.xlsx",
            "timesheet_PiedPiper_20220101-20221231.pdf",
            "timesheet_PiedPiper_20220101-20221231.csv"
        )
        upload(jsonFilename, jsonFileContent)
        val s3Event = createS3Event(jsonFilename)

        RestAssured.given()
            .contentType("application/json")
            .accept("application/json")
            .body(s3Event)
            .`when`()
            .post()
            .then()
            .statusCode(200)
            .body("persistenceResults.size()", equalTo(3))

        expectedFilenames.forEach {
            val file = download("${it.substringAfter(".")}/$it")
            assertThat(file.size).isGreaterThan(0)

        }
    }

    private fun readResource(filename: String): ByteArray = ResourcesReader.bytesFromResourceFile("e2e/$filename")

    private fun createS3Event(fileLocation: String) =
        S3EventNotification(listOf(S3EventNotificationRecord((S3Entity(S3ObjectEntity(fileLocation))))))

    private fun createBucket() {
        s3Client.createBucket(CreateBucketRequest.builder().bucket(bucket).build())
    }

    private fun upload(key: String, content: ByteArray): PutObjectResponse {
        return s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build(),
            RequestBody.fromBytes(content)
        )
    }

    private fun download(key: String): ByteArray {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val response = s3Client.getObject(request)
        return response.readAllBytes()
    }
}
