package dev.hertlein.timesheetwizard.app.scaleway

import dev.hertlein.timesheetwizard.app.scaleway.util.TestProfiles.TESTCONTAINERS
import dev.hertlein.timesheetwizard.app.scaleway.util.TestcontainersConfiguration
import dev.hertlein.timesheetwizard.core.AbstractApplicationE2ETest
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

@DisplayName("Scaleway Application")
@SpringBootTest
@ActiveProfiles(TESTCONTAINERS)
@Import(TestcontainersConfiguration::class)
class ScalewayApplicationE2ETest : AbstractApplicationE2ETest() {

    companion object {

        @DynamicPropertySource
        @JvmStatic
        fun clockifyProperties(registry: DynamicPropertyRegistry) {
            registry.add("timesheet-wizard.import.clockify.reports-url") { "$MOCK_SERVER_HOST:$MOCK_SERVER_PORT" }
            registry.add("timesheet-wizard.import.clockify.api-key") { "an-api-key" }
            registry.add("timesheet-wizard.import.clockify.workspace-id") { "a-workspace-id" }
        }
    }

    @Autowired
    private lateinit var commandLineRunner: ScalewayCommandLineRunner

    @Autowired
    private lateinit var repository: ScalewayObjectStorageRepository

    @Test
    fun `should import and export timesheets to Scaleway Object Storage`() {
        executeTest(repository, this::run)
    }

    private fun run() {
        val input = """{"customerIds": ["1000"], "dateRangeType": "CUSTOM_YEAR", "dateRange": "2022"}"""
        commandLineRunner.run(input)
    }
}