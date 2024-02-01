package dev.hertlein.timesheetwizard.importer.adapter.clockify.component

import dev.hertlein.timesheetwizard.importer.adapter.clockify.model.ResponseBody
import dev.hertlein.timesheetwizard.importer.adapter.clockify.model.ResponseBody.TimeEntry
import dev.hertlein.timesheetwizard.importer.model.TimesheetEntry
import jakarta.inject.Singleton
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.time.DurationUnit
import kotlin.time.toDuration

private val FORMAT = DateTimeFormatter.ISO_OFFSET_DATE_TIME

@Singleton
class ResponseBodyMapper {

    fun toTimesheetEntries(responseBody: ResponseBody): List<TimesheetEntry> =
        responseBody
            .timeentries
            .map {
                TimesheetEntry.of(
                    it.projectName,
                    it.description,
                    it.tags?.map { tag -> TimeEntry.Tag(tag.name).name } ?: emptyList(),
                    LocalDateTime.parse(it.timeInterval.start, FORMAT),
                    LocalDateTime.parse(it.timeInterval.end, FORMAT),
                    it.timeInterval.duration.toDuration(DurationUnit.SECONDS)
                )
            }
}
