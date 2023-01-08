package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.RequestBody
import dev.hertlein.timesheetwizard.importclockify.model.Customer
import jakarta.inject.Singleton
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Singleton
class RequestBodyFactory {

    fun create(customer: Customer, dateRange: ClosedRange<LocalDate>): RequestBody {
        val clientsFilter = RequestBody.ClientsFilter(customer.clockifyId)
        val dates = dateRange
            .let { toDateTimeRange(it) }
            .let { toStringRange(it) }

        return RequestBody(clientsFilter, dates.start, dates.endInclusive)
    }

    private fun toDateTimeRange(range: ClosedRange<LocalDate>) =
        LocalDateTime.of(range.start, LocalTime.MIN)..
                LocalDateTime.of(range.endInclusive, LocalTime.MAX)

    private fun toStringRange(range: ClosedRange<LocalDateTime>) =
        format(range.start)..
                format(range.endInclusive)

    private fun format(dateTime: LocalDateTime): String = dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
}
