package dev.hertlein.timesheetwizard.generateexports.application

import dev.hertlein.timesheetwizard.generateexports.application.factory.TimesheetDocumentFactory
import dev.hertlein.timesheetwizard.generateexports.application.port.PersistencePort
import dev.hertlein.timesheetwizard.generateexports.application.port.PersistenceResult
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@ApplicationScoped
class OrchestrationService(
    private val persistencePort: PersistencePort,
    private val documentFactories: Instance<TimesheetDocumentFactory>
) {

    fun execute(timesheetLocation: String): List<PersistenceResult> {
        logger.debug { "Generating documents for timesheet at location '$timesheetLocation'..." }

        val timesheet = persistencePort.findTimesheetByURI(timesheetLocation)

        val persistenceResults = documentFactories.map {
            val document = it.apply(timesheet)
            persistencePort.save(document)
        }

        return persistenceResults.also {
            logger.debug { "Generated documents for timesheet. Persisted as: ${it.joinToString()}." }
        }
    }
}
