package dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify

import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.component.ReportClient
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.component.RequestBodyFactory
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.component.ResponseBodyMapper
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.import_.core.port.TimesheetDataFetchPort
import dev.hertlein.timesheetwizard.shared.configloader.ClockifyIdsLoader
import dev.hertlein.timesheetwizard.shared.model.Customer
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import mu.KotlinLogging
import org.springframework.stereotype.Component
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Component
class ClockifyAdapter(
    private val clockifyIdsLoader: ClockifyIdsLoader,
    private val reportClient: ReportClient,
    private val requestBodyFactory: RequestBodyFactory,
    private val entityMapper: ResponseBodyMapper
) : TimesheetDataFetchPort {

    private val customerIdsToClockifyIds: Map<String, String> by lazy { clockifyIdsLoader.loadClockifyIds() }

    override fun fetchTimesheet(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet? {
        val clockifyId = customerIdsToClockifyIds[customer.id.value]
        if (clockifyId == null) {
            logger.warn { "No Clockify customer found for Id ${customer.id.value}." }
            return null
        }

        val requestBody = requestBodyFactory.requestBodyFrom(clockifyId, dateRange)
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
