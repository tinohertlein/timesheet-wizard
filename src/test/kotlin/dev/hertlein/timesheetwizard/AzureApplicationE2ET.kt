package dev.hertlein.timesheetwizard

import com.azure.storage.blob.BlobContainerClient
import dev.hertlein.timesheetwizard.util.AzureBlobOperations
import dev.hertlein.timesheetwizard.util.SpringTestProfiles
import dev.hertlein.timesheetwizard.util.TestcontainersConfiguration
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles


@DisplayName("Azure Application")
@ActiveProfiles(SpringTestProfiles.TESTCONTAINERS, "azure")
@SpringBootTest
@Import(TestcontainersConfiguration::class)
class AzureApplicationE2ET : AbstractApplicationE2E() {

    @Autowired
    private lateinit var blobClient: BlobContainerClient

    @Test
    fun `should import and export timesheets to Azure Blob Storage`() {
        executeTest(this::uploadToBlobStorage, this::downloadFromBlobStorage)
    }

    private fun uploadToBlobStorage(key: String, bytes: ByteArray) {
        AzureBlobOperations.upload(blobClient, key, bytes)
    }

    private fun downloadFromBlobStorage(key: String): ByteArray {
        return AzureBlobOperations.download(blobClient, key)
    }
}