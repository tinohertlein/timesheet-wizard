package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.core._import.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core._import.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.util.CloudPersistenceInMemory
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("Application")
class ApplicationE2E : AbstractApplicationE2E() {

    private val cloudPersistence = CloudPersistenceInMemory()
    private val clockifyConfig = object : ClockifyConfig {
        override val reportsUrl: String
            get() = "${MOCK_SERVER_HOST}:${MOCK_SERVER_PORT}"
        override val apiKey: String
            get() = " an-api-key"
        override val workspaceId: String
            get() = "a-workspace-id"
    }

    private val importService = Core.bootstrap(cloudPersistence, clockifyConfig)

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