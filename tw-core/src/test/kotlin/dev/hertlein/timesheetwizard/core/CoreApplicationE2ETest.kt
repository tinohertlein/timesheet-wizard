package dev.hertlein.timesheetwizard.core

import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportingStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core.util.RepositoryInMemory
import dev.hertlein.timesheetwizard.spi.app.ClockifyConfig
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test


@DisplayName("Core Application")
class CoreApplicationE2ETest : AbstractApplicationE2ETest() {

    private val repository = RepositoryInMemory()
    private val clockifyConfig = object : ClockifyConfig {
        override val reportsUrl: String
            get() = "${MOCK_SERVER_HOST}:${MOCK_SERVER_PORT}"
        override val apiKey: String
            get() = " an-api-key"
        override val workspaceId: String
            get() = "a-workspace-id"
    }

    private val eventBus = Core.bootstrap(repository, clockifyConfig)

    @Test
    fun `should import and export timesheets to memory`() {
        executeTest(this::upload, this::download, this::run)
    }

    private fun upload(key: String, bytes: ByteArray) {
        repository.upload(key, bytes)
    }

    private fun download(key: String): ByteArray {
        return repository.download(key)
    }

    private fun run() {
        eventBus.post(ImportingStartedEvent(ImportParams(listOf("1000"), DateRangeType.CUSTOM_YEAR, "2022")))

    }
}