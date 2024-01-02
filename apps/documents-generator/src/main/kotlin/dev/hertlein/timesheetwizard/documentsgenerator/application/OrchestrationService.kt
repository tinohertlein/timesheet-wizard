package dev.hertlein.timesheetwizard.documentsgenerator.application

import dev.hertlein.timesheetwizard.documentsgenerator.application.config.Contact
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistencePort
import dev.hertlein.timesheetwizard.documentsgenerator.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.documentsgenerator.spi.TimesheetDocumentFactory
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.inject.Instance
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@ApplicationScoped
class OrchestrationService(
    private val contact: Contact,
    private val persistencePort: PersistencePort,
    private val documentFactories: Instance<TimesheetDocumentFactory>
) {

    fun execute(timesheetLocation: String): List<PersistenceResult> {
        logger.debug { "Generating documents for timesheet at location '$timesheetLocation'..." }

        val timesheet = persistencePort.findTimesheetByURI(timesheetLocation)

        val factoriesForCustomer = documentFactories.filter { it.canHandle(timesheet.customer) }

        check(factoriesForCustomer.isNotEmpty()) {
            throw IllegalStateException(
                "Could not find factories to generate documents for customer ${timesheet.customer}"
            )
        }

        val persistenceResults = factoriesForCustomer
            .map {
                val document = it.create(contact.toContactDetails(), timesheet)
                persistencePort.save(document)
            }

        return persistenceResults.also {
            logger.debug { "Generated documents for timesheet. Persisted as: ${it.joinToString()}." }
        }
    }
}
