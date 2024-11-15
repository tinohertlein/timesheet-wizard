package dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.component

import dev.hertlein.timesheetwizard.core._import.core.adapter.outgoing.clockify.model.RequestBody
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
internal class RequestBodyFactory {

    fun requestBodyFrom(clockifyId: String, dateRange: ClosedRange<LocalDate>): RequestBody {
        val clientsFilter = RequestBody.ClientsFilter(clockifyId)
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
