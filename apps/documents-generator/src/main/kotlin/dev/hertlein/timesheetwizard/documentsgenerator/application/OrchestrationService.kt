package dev.hertlein.timesheetwizard.documentsgenerator.application

import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistencePort
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.documentsgenerator.spi.TimesheetDocumentFactory
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
            val document = it.create(timesheet)
            persistencePort.save(document)
        }

        return persistenceResults.also {
            logger.debug { "Generated documents for timesheet. Persisted as: ${it.joinToString()}." }
        }
    }
}
