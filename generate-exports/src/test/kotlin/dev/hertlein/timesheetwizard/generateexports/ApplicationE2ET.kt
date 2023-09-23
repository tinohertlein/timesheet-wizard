package dev.hertlein.timesheetwizard.generateexports

import dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model.S3Entity
import dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model.S3EventNotification
import dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model.S3EventNotificationRecord
import dev.hertlein.timesheetwizard.generateexports.adapter.lambda.model.S3ObjectEntity
import dev.hertlein.timesheetwizard.generateexports.util.ResourcesReader
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.xssf.usermodel.XSSFSheet
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import jakarta.inject.Inject

@QuarkusTest
@DisplayName("Application")
internal class ApplicationE2ET {

    @Inject
    lateinit var s3Client: S3Client

    @ConfigProperty(name = "aws.s3.bucket")
    lateinit var bucket: String

    @Test
    fun `should generate an Excel file from json`() {
        createBucket()
        val jsonFilename = "PiedPiper_2022-01-01_2022-12-31_e47dc4a0-2899-41a7-a390-a5c6152f2e42.json"
        val jsonFileContent = readResource(jsonFilename)
        val excelFilename = "timesheet_PiedPiper_20220101-20221231.xlsx"
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
            .body("persistenceResults[0].uri", CoreMatchers.equalTo("xlsx/$excelFilename"))

        val generatedExcel = download("xlsx/$excelFilename")
        val expectedExcel = readResource(excelFilename)

        ExcelVerification.assertEquals(generatedExcel, expectedExcel)
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

    object ExcelVerification {

        fun assertEquals(actual: ByteArray, expected: ByteArray) {
            val actualSheet = sheetFrom(actual)
            val expectedSheet = sheetFrom(expected)
            val cellFormatter = DataFormatter()
            val maxRowIndex = expectedSheet.lastRowNum
            val maxColumnIndex = 4

            assertThat(actualSheet.lastRowNum).isEqualTo(expectedSheet.lastRowNum)

            (0..maxRowIndex).forEach { rowIndex: Int ->
                (0..maxColumnIndex).forEach { columnIndex: Int ->
                    val actualCell = cellValue(cellFormatter, actualSheet, rowIndex, columnIndex)
                    val expectedCell = cellValue(cellFormatter, expectedSheet, rowIndex, columnIndex)

                    assertThat(actualCell).withFailMessage(
                        """Cells in 
                            |row %d, column %d (starting both at 0) 
                            |do not match: %s (actual) <-> %s (expected).""".trimMargin(),
                        rowIndex,
                        columnIndex,
                        actualCell,
                        expectedCell
                    ).isEqualTo(expectedCell)
                }
            }
        }

        private fun sheetFrom(byteArray: ByteArray): XSSFSheet = XSSFWorkbook(byteArray.inputStream()).getSheetAt(0)

        private fun cellValue(formatter: DataFormatter, sheet: XSSFSheet, rowIndex: Int, columnIndex: Int): String =
            formatter.formatCellValue(sheet.getRow(rowIndex).getCell(columnIndex))
    }
}
