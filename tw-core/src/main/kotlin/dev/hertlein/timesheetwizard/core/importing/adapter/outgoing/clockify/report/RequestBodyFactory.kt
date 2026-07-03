package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class RequestBodyFactory {

    fun requestBodyFrom(clockifyId: String, dateRange: ClosedRange<LocalDate>): RequestBody =
        requestBodyFrom(dateRange, RequestBody.ClientsFilter(clockifyId))

    fun requestBodyFrom(dateRange: ClosedRange<LocalDate>): RequestBody = requestBodyFrom(dateRange, null)

    private fun requestBodyFrom(dateRange: ClosedRange<LocalDate>, clientsFilter: RequestBody.ClientsFilter?): RequestBody {
        val dates = dateRange
            .let { toDateTimeRange(it) }
            .let { toStringRange(it) }

        return RequestBody(clientsFilter, dates.start, dates.endInclusive)
    }

    private fun toDateTimeRange(range: ClosedRange<LocalDate>) =
        LocalDateTime.of(range.start, LocalTime.MIN)..LocalDateTime.of(range.endInclusive, LocalTime.MAX)

    private fun toStringRange(range: ClosedRange<LocalDateTime>) =
        format(range.start)..format(range.endInclusive)

    private fun format(dateTime: LocalDateTime): String = dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
}