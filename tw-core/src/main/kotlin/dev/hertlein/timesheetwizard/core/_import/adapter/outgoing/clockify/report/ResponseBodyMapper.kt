package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import org.springframework.stereotype.Component
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.Duration.Companion.seconds

private val FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME

@Component
internal class ResponseBodyMapper {

    fun toTimesheetEntries(responseBody: ResponseBody?): List<Timesheet.Entry> =
        responseBody
            ?.timeentries
            ?.map { toTimesheetEntry(it) }
            ?: emptyList()

    private fun toTimesheetEntry(it: ResponseBody.TimeEntry) =
        Timesheet.Entry.of(
            it.projectName,
            it.description,
            it.tags?.map { tag -> ResponseBody.TimeEntry.Tag(tag.name).name } ?: emptyList(),
            OffsetDateTime.parse(it.timeInterval.start, FORMAT),
            OffsetDateTime.parse(it.timeInterval.end, FORMAT),
            it.timeInterval.duration.seconds
        )
}