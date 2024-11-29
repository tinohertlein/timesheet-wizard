package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify

import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.ReportClient
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.RequestBodyFactory
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.ResponseBodyMapper
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report.RequestBody
import dev.hertlein.timesheetwizard.core._import.core.port.TimesheetDataFetchPort
import dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.config.ClockifyId
import dev.hertlein.timesheetwizard.core.shared.model.Customer
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Component
internal class ClockifyAdapter(
    private val clockifyIdsLoader: ClockifyIdsLoader,
    private val reportClient: ReportClient,
    private val requestBodyFactory: RequestBodyFactory,
    private val entityMapper: ResponseBodyMapper
) : TimesheetDataFetchPort {

    private val clockifyIds: List<ClockifyId> by lazy { clockifyIdsLoader.loadClockifyIds() }

    override fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet? {
        val clockifyId = clockifyIds.firstOrNull { customer.id.value == it.customerId }
        if (clockifyId == null) {
            logger.warn { "No Clockify id found for customer id ${customer.id.value}." }
            return null
        }

        val requestBody = requestBodyFactory.requestBodyFrom(clockifyId.clockifyId, dateRange)
        val timesheet = Timesheet(customer, dateRange)

        return populateTimesheet(timesheet, requestBody)
    }

    private tailrec fun populateTimesheet(timesheet: Timesheet, requestBody: RequestBody): Timesheet {
        logger.debug { "Fetching report page ${requestBody.page()} from Clockify for customer ${timesheet.customer.id.value}..." }
        val responseBody = reportClient.fetchReport(requestBody)
        val entries = entityMapper.toTimesheetEntries(responseBody)
        logger.debug { "Fetched ${entries.size} report entries with page ${requestBody.page()} from Clockify for customer ${timesheet.customer.id.value}." }

        return if (entries.isEmpty()) {
            timesheet
        } else {
            populateTimesheet(timesheet.add(entries), requestBody.withIncrementedPageNumber())
        }
    }
}
