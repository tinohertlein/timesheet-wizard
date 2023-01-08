package dev.hertlein.timesheetwizard.importclockify.adapter.clockify

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component.ReportClient
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component.RequestBodyFactory
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component.ResponseBodyMapper
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.importclockify.application.port.ImportPort
import dev.hertlein.timesheetwizard.importclockify.model.Customer
import dev.hertlein.timesheetwizard.importclockify.model.Timesheet
import jakarta.inject.Singleton
import mu.KotlinLogging
import java.time.LocalDate

private val logger = KotlinLogging.logger {}

@Singleton
class ClockifyImportAdapter(
    private val reportClient: ReportClient,
    private val requestBodyFactory: RequestBodyFactory,
    private val entityMapper: ResponseBodyMapper
) : ImportPort {

    override fun import(customer: Customer, dateRange: ClosedRange<LocalDate>): Timesheet {
        val requestBody = requestBodyFactory.create(customer, dateRange)
        val timesheet = Timesheet(customer, dateRange)

        return populateTimesheet(timesheet, requestBody)
    }

    private tailrec fun populateTimesheet(timesheet: Timesheet, requestBody: RequestBody): Timesheet {
        logger.debug { "Fetching report page ${requestBody.page()} from Clockify..." }
        val responseBody = reportClient.fetchReport(requestBody)
        val entries = entityMapper.toTimesheetEntries(responseBody)
        logger.debug { "Fetched ${entries.size} report entries with page ${requestBody.page()} from Clockify." }

        return if (entries.isEmpty()) {
            timesheet
        } else {
            populateTimesheet(timesheet.add(entries), requestBody.withIncrementedPageNumber())
        }
    }
}
