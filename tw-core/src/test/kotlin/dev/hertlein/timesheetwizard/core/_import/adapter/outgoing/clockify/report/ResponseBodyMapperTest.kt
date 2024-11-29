package dev.hertlein.timesheetwizard.core._import.adapter.outgoing.clockify.report

import dev.hertlein.timesheetwizard.core._import.core.model.ImportTimesheet
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours

private const val SECONDS_PER_HOUR = 3600

@DisplayName("ResponseBodyMapper")
internal class ResponseBodyMapperTest {

    private val zoneOffset = ZoneOffset.ofHours(1)

    private val aProject = "a project"
    private val anotherProject = "another project"

    private val aTask = "a task"
    private val anotherTask = "another task"

    private val someTagsAsStrings = listOf("onsite")
    private val someTagsAsTags = someTagsAsStrings.map { ResponseBody.TimeEntry.Tag(it) }

    private val aDateLowerAsDateTime = OffsetDateTime.of(2022, 8, 1, 8, 0, 0, 0, zoneOffset)
    private val aDateLowerAsString = "2022-08-01T08:00:00+01:00"
    private val aDateUpperAsDateTime = OffsetDateTime.of(2022, 8, 1, 10, 0, 0, 0, zoneOffset)
    private val aDateUpperAsString = "2022-08-01T10:00:00+01:00"

    private val anotherDateLowerAsDateTime = OffsetDateTime.of(2022, 9, 30, 12, 0, 0, 0, zoneOffset)
    private val anotherDateLowerAsString = "2022-09-30T12:00:00+01:00"
    private val anotherDateUpperAsDateTime = OffsetDateTime.of(2022, 9, 30, 15, 0, 0, 0, zoneOffset)
    private val anotherDateUpperAsString = "2022-09-30T15:00:00+01:00"

    @Nested
    inner class ToExportTimesheetEntries {

        private val responseBodyMapper = ResponseBodyMapper()

        @Test
        fun `should map to timesheet entries `() {
            val responseBody = ResponseBody(input())

            val timesheetEntries = responseBodyMapper.toTimesheetEntries(responseBody)

            Assertions.assertThat(timesheetEntries).isEqualTo(expected())
        }
    }

    private fun input(): List<ResponseBody.TimeEntry> = listOf(
        ResponseBody.TimeEntry(
            aProject,
            aTask,
            someTagsAsTags,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        ResponseBody.TimeEntry(
            aProject,
            aTask,
            someTagsAsTags,
            ResponseBody.TimeEntry.TimeInterval(anotherDateLowerAsString, anotherDateUpperAsString, toSeconds(3))
        ),
        ResponseBody.TimeEntry(
            aProject,
            anotherTask,
            someTagsAsTags,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        ResponseBody.TimeEntry(
            anotherProject,
            aTask,
            someTagsAsTags,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        ResponseBody.TimeEntry(
            anotherProject,
            anotherTask,
            someTagsAsTags,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
    )

    private fun expected(): List<ImportTimesheet.Entry> = listOf(
        ImportTimesheet.Entry.of(
            aProject,
            aTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.hours
        ),
        ImportTimesheet.Entry.of(
            aProject,
            aTask,
            someTagsAsStrings,
            anotherDateLowerAsDateTime,
            anotherDateUpperAsDateTime,
            3.hours
        ),
        ImportTimesheet.Entry.of(
            aProject,
            anotherTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.hours
        ),
        ImportTimesheet.Entry.of(
            anotherProject,
            aTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.hours
        ),
        ImportTimesheet.Entry.of(
            anotherProject,
            anotherTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.hours
        )
    )

    private fun toSeconds(hours: Int) = (hours * SECONDS_PER_HOUR).toLong()
}