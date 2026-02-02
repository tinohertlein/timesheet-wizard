package dev.hertlein.timesheetwizard.app.gcp

import com.google.cloud.storage.BucketInfo
import com.google.cloud.storage.Storage
import dev.hertlein.timesheetwizard.spi.cloud.Repository
import jakarta.inject.Inject
import jakarta.inject.Singleton
import mu.KotlinLogging
import org.eclipse.microprofile.config.inject.ConfigProperty

private val logger = KotlinLogging.logger {}

@Singleton
class GcpStorageRepository(
    @param:ConfigProperty(name = "timesheet-wizard.gcp.storage.bucket")
    private val bucket: String
) : Repository {

    fun createBucketIfNotExists(bucket: String = this.bucket) {
        if (storage.list().values.none { it.name == bucket }) {
            storage.create(BucketInfo.newBuilder(bucket).build())
        }
    }

    @Inject
    private lateinit var storage: Storage

    override fun type() = "GoogleCloudStorage"

    override fun root() = bucket

    override fun upload(key: String, content: ByteArray) {
        storage.get(bucket).create(key, content).also {
            logger.info { "Downloaded content from ${location(key)} " }
        }
    }

    override fun download(key: String): ByteArray =
        storage.get(bucket).get(key).getContent().also {
            logger.info { "Uploaded content to ${location(key)} " }
        }
}