package dev.hertlein.timesheetwizard.importer.application

import dev.hertlein.timesheetwizard.importer.application.config.ImportConfig
import dev.hertlein.timesheetwizard.importer.application.factory.CustomerFactory
import dev.hertlein.timesheetwizard.importer.application.factory.DateTimeFactory
import dev.hertlein.timesheetwizard.importer.application.port.ImportPort
import dev.hertlein.timesheetwizard.importer.application.port.PersistencePort
import dev.hertlein.timesheetwizard.importer.application.port.PersistenceResult
import dev.hertlein.timesheetwizard.importer.model.Customer
import jakarta.inject.Singleton
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

@Singleton
class OrchestrationService(
    private val customerFactory: CustomerFactory,
    private val dateTimeFactory: DateTimeFactory,
    private val importPort: ImportPort,
    private val persistencePort: PersistencePort
) {

    fun execute(importConfig: ImportConfig): List<PersistenceResult> {
        return customerFactory.create(importConfig.customerIds)
            .map { customer: Customer ->
                val dateRange = dateTimeFactory.create(importConfig.dateRangeType, importConfig.dateRange)

                logger.debug { "Importing timesheet for customer $customer and date range $dateRange..." }

                val timesheet = importPort.import(customer, dateRange)
                logger.debug { "Fetched $timesheet" }
                val persistenceResult = persistencePort.save(timesheet)

                logger.debug {
                    "Imported timesheet with ${timesheet.entries.size} entries for customer " +
                            "$customer and date range $dateRange. Persisted as: $persistenceResult"
                }
                persistenceResult
            }
    }
}
