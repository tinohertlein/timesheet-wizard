package dev.hertlein.timesheetwizard.core._import.domain.service

import dev.hertlein.timesheetwizard.core._import.domain.model.ImportParams
import dev.hertlein.timesheetwizard.core._import.domain.port.EventPublishPort
import dev.hertlein.timesheetwizard.core._import.domain.port.TimesheetSourcePort
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

interface ImportService {
    fun import(importParams: ImportParams)
}

internal class ImportServiceImpl(
    private val customerFactory: CustomerFactory,
    private val dateTimeFactory: DateTimeFactory,
    private val timesheetSourcePort: TimesheetSourcePort,
    private val eventPublishPort: EventPublishPort
) : ImportService {

    override fun import(importParams: ImportParams) {
        val customers = customerFactory.customersFrom(importParams.customerIds)
        val dateRange = dateTimeFactory.create(importParams.dateRangeType, importParams.dateRange)

        logger.info { "Importing timesheets for customers '${customers.joinToString { it.id.value }}' and date range $dateRange..." }

        customers.forEach { customer ->
            logger.info { "Importing timesheet for customer '${customer.id.value}' and date range $dateRange..." }
            timesheetSourcePort
                .fetchTimesheet(customer, dateRange)
                ?.also { logger.info { "Imported timesheet for customer '${customer.id.value}' and date range $dateRange." } }
                ?.also { eventPublishPort.publish(it) }
        }
    }
}