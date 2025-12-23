package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.core._import.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core._import.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core._import.domain.service.ImportServiceImpl
import dev.hertlein.timesheetwizard.spi.cloud.CloudPersistence
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


@DisplayName("Application")
@SpringBootTest(classes = [TestApplication::class])
class ApplicationE2E : AbstractApplicationE2E() {

    @Autowired
    private lateinit var cloudPersistence: CloudPersistence

    @Autowired
    private lateinit var importService: ImportServiceImpl

    @Test
    fun `should import and export timesheets to memory`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        cloudPersistence.upload(key, bytes)
    }

    private fun download(key: String): ByteArray {
        return cloudPersistence.download(key)
    }

    private fun run() {
        importService.import(ImportParams(listOf("1000"), DateRangeType.CUSTOM_YEAR, "2022"))
    }
}