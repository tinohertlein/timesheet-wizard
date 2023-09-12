package dev.hertlein.timesheetwizard.generateexcel.application

import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistencePort
import dev.hertlein.timesheetwizard.generateexcel.application.port.PersistenceResult
import mu.KotlinLogging
import jakarta.enterprise.context.ApplicationScoped

private val logger = KotlinLogging.logger {}

@ApplicationScoped
class OrchestrationService(
    private val persistencePort: PersistencePort,
    private val excelFactory: ExcelFactory
) {

    fun execute(timesheetLocation: String): PersistenceResult {
        logger.debug { "Generating Excel for timesheet at location '$timesheetLocation'..." }

        val timesheet = persistencePort.findTimesheetByURI(timesheetLocation)

        val excel = excelFactory.create(timesheet)
        val persistenceResult = persistencePort.save(excel)

        return persistenceResult.also {
            logger.debug { "Generated Excel for timesheet. Persisted as: $it." }
        }
    }
}
