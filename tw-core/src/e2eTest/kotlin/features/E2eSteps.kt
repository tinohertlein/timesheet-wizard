package features

import dev.hertlein.timesheetwizard.core.AbstractE2ESteps
import dev.hertlein.timesheetwizard.core.InMemoryRepository
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_HOST
import dev.hertlein.timesheetwizard.core.MOCK_SERVER_PORT
import dev.hertlein.timesheetwizard.core.anticorruption.Core
import dev.hertlein.timesheetwizard.core.importing.adapter.incoming.eventing.ImportStartedEvent
import dev.hertlein.timesheetwizard.core.importing.domain.model.DateRangeType
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportParams
import dev.hertlein.timesheetwizard.spi.ClockifyConfig
import io.cucumber.java8.En

class E2eSteps : En, AbstractE2ESteps() {

    private val clockifyConfig = object : ClockifyConfig {
        override val reportsUrl: String
            get() = "${MOCK_SERVER_HOST}:${MOCK_SERVER_PORT}"
        override val apiKey: String
            get() = " an-api-key"
        override val workspaceId: String
            get() = "a-workspace-id"
    }
    
    private val repository = InMemoryRepository()
    private val eventBus = Core.bootstrap(repository, clockifyConfig)

    init {

        Given("timesheet entries are logged in Clockify for customer with id 1000") {
            given(repository)
        }

        When("the application is started with customer id 1000 and date range 'CUSTOM_YEAR' with a value of '2022'") {
            eventBus.post(ImportStartedEvent(ImportParams(listOf("1000"), DateRangeType.CUSTOM_YEAR, "2022")))
        }

        Then("the timesheet entries are imported from Clockify, transformed and then exported to a memory persistence store") {
            then(repository)
        }
    }
}