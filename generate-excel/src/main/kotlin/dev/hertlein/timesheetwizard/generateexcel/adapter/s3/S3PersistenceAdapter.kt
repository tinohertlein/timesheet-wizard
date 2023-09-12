package dev.hertlein.timesheetwizard.generateexcel.adapter.s3

import dev.hertlein.timesheetwizard.generateexcel.adapter.s3.component.JsonMapper
import dev.hertlein.timesheetwizard.generateexcel.adapter.s3.component.FilenameFactory
import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistencePort
import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistenceTarget
import dev.hertlein.timesheetwizard.generateexcel.model.Excel
import dev.hertlein.timesheetwizard.generateexcel.model.Timesheet
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectResponse
import jakarta.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}

private const val EXCEL_PREFIX = "xlsx"

@ApplicationScoped
class S3PersistenceAdapter(
    @ConfigProperty(name = "aws.s3.bucket")
    private val bucket: String,
    private val s3Client: S3Client,
    private val jsonMapper: JsonMapper,
    private val filenameFactory: FilenameFactory
) : PersistencePort {

    override fun findTimesheetByURI(uri: String): Timesheet {
        val json = download(uri)
        return jsonMapper.toTimesheetEntity(json)
    }

    override fun save(excel: Excel): PersistenceResult {
        logger.debug { "Persisting Excel..." }

        val filename = filenameFactory.create(EXCEL_PREFIX, excel)

        upload(filename, excel.content)

        return PersistenceResult(PersistenceTarget.S3, filename)
            .also { logger.debug { "Persisted Excel as '$it'" } }
    }

    private fun download(key: String): String {
        val request = GetObjectRequest.builder().bucket(bucket).key(key).build()
        val response = s3Client.getObject(request)
        return String(response.readAllBytes())
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
}
