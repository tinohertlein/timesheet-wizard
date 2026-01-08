package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify

import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config.ClockifyId
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.config.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.HttpReportClient
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.RequestBody
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.RequestBodyFactory
import dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report.ResponseBodyMapper
import dev.hertlein.timesheetwizard.core.importing.domain.model.Customer
import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
import dev.hertlein.timesheetwizard.core.importing.domain.port.TimesheetSourcePort
import mu.KotlinLogging
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

internal class ClockifyAdapter(
    private val clockifyIdsLoader: ClockifyIdsLoader,
    private val reportClient: HttpReportClient,
    private val requestBodyFactory: RequestBodyFactory,
    private val entityMapper: ResponseBodyMapper
) : TimesheetSourcePort {

    private val clockifyIds: List<ClockifyId> by lazy { clockifyIdsLoader.loadClockifyIds() }

    override fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): ImportTimesheet {
        val clockifyId = clockifyIds.firstOrNull { customer.id.value == it.customerId }
        require(clockifyId != null) { "No Clockify id found for customer id ${customer.id.value}." }

        val requestBody = requestBodyFactory.requestBodyFrom(clockifyId.clockifyId, dateRange)
        val timesheet = ImportTimesheet(customer, dateRange)

        return populateTimesheet(timesheet, requestBody)
    }

    private tailrec fun populateTimesheet(timesheet: ImportTimesheet, requestBody: RequestBody): ImportTimesheet {
        logger.debug { "Fetching report page ${requestBody.page()} from Clockify for customer ${timesheet.customer.id}..." }
        val responseBody = reportClient.fetchReport(requestBody)
        val entries = entityMapper.toTimesheetEntries(responseBody)
        logger.debug { "Fetched page ${requestBody.page()} with ${entries.size} report entries from Clockify for customer ${timesheet.customer.id}." }

        return if (entries.isEmpty()) {
            timesheet
        } else {
            populateTimesheet(timesheet.add(entries), requestBody.withIncrementedPageNumber())
        }
    }
}
