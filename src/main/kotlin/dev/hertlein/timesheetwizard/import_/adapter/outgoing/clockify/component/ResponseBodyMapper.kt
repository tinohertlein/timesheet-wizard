package dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.component

import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.model.ResponseBody
import dev.hertlein.timesheetwizard.import_.adapter.outgoing.clockify.model.ResponseBody.TimeEntry
import dev.hertlein.timesheetwizard.shared.model.Timesheet
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

private val FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME

@Component
class ResponseBodyMapper {

    fun toTimesheetEntries(responseBody: ResponseBody?): List<Timesheet.Entry> =
        responseBody
            ?.timeentries
            ?.map { toTimesheetEntry(it) }
            ?: emptyList()

    private fun toTimesheetEntry(it: TimeEntry) =
        Timesheet.Entry.of(
            it.projectName,
            it.description,
            it.tags?.map { tag -> TimeEntry.Tag(tag.name).name } ?: emptyList(),
            OffsetDateTime.parse(it.timeInterval.start, FORMAT),
            OffsetDateTime.parse(it.timeInterval.end, FORMAT),
            it.timeInterval.duration.seconds
        )
}
