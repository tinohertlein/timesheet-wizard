package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody.TimeEntry
import dev.hertlein.timesheetwizard.importclockify.model.TimesheetEntry
import jakarta.inject.Singleton
import java.time.format.DateTimeFormatter
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import java.time.LocalDate.parse as toLocalDate

@Singleton
class ResponseBodyMapper {

    fun toTimesheetEntries(responseBody: ResponseBody): List<TimesheetEntry> =
        responseBody
            .timeentries
            .map {
                TimesheetEntry(
                    it.projectName,
                    it.description,
                    it.tags.map { tag -> TimeEntry.Tag(tag.name).name },
                    toLocalDate(it.timeInterval.start, DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                    it.timeInterval.duration.toDuration(DurationUnit.SECONDS)
                )
            }
}
