package dev.hertlein.timesheetwizard.import_.core

import dev.hertlein.timesheetwizard.import_.core.model.ImportParams
import dev.hertlein.timesheetwizard.import_.core.port.EventPublishPort
import dev.hertlein.timesheetwizard.import_.core.port.TimesheetDataFetchPort
import dev.hertlein.timesheetwizard.shared.CustomerFactory
import mu.KotlinLogging
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}

@Service
class ImportService(
    private val customerFactory: CustomerFactory,
    private val dateTimeFactory: DateTimeFactory,
    private val timesheetDataFetchPort: TimesheetDataFetchPort,
    private val eventPublishPort: EventPublishPort
) {

    fun import(importParams: ImportParams) {
        val customers = customerFactory.customersFrom(importParams.customerIds)
        val dateRange = dateTimeFactory.create(importParams.dateRangeType, importParams.dateRange)

        logger.info { "Importing timesheets for customers '${customers.joinToString { it.id.value }}' and date range $dateRange..." }

        customers.forEach { customer ->
            logger.info { "Importing timesheet for customer '${customer.id.value}' and date range $dateRange..." }
            timesheetDataFetchPort
                .fetchTimesheet(customer, dateRange)
                ?.also { logger.info { "Imported timesheet for customer '${customer.id.value}' and date range $dateRange." } }
                ?.also { eventPublishPort.publish(it) }
        }
    }
}