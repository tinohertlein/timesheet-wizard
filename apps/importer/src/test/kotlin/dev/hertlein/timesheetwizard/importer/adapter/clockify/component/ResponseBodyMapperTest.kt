package dev.hertlein.timesheetwizard.importer.adapter.clockify.component

import dev.hertlein.timesheetwizard.importer.adapter.clockify.model.ResponseBody
import dev.hertlein.timesheetwizard.importer.adapter.clockify.model.ResponseBody.TimeEntry
import dev.hertlein.timesheetwizard.importer.adapter.clockify.model.ResponseBody.TimeEntry.TimeInterval
import dev.hertlein.timesheetwizard.importer.model.TimesheetEntry
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTimeConstants
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.time.DurationUnit.HOURS
import kotlin.time.toDuration

@DisplayName("ResponseBodyMapper")
internal class ResponseBodyMapperTest {

    private val aProject = "a project"
    private val anotherProject = "another project"

    private val aTask = "a task"
    private val anotherTask = "another task"

    private val someTagsAsStrings = listOf("onsite")
    private val someTagsAsTags = someTagsAsStrings.map { TimeEntry.Tag(it) }

    private val aDateLowerAsDateTime = LocalDateTime.of(2022, 8, 1, 8, 0)
    private val aDateLowerAsString = "2022-08-01T08:00:00+01:00"
    private val aDateUpperAsDateTime = LocalDateTime.of(2022, 8, 1, 10, 0, 0)
    private val aDateUpperAsString = "2022-08-01T10:00:00+01:00"

    private val anotherDateLowerAsDateTime = LocalDateTime.of(2022, 9, 30, 12, 0)
    private val anotherDateLowerAsString = "2022-09-30T12:00:00+01:00"
    private val anotherDateUpperAsDateTime = LocalDateTime.of(2022, 9, 30, 15, 0, 0)
    private val anotherDateUpperAsString = "2022-09-30T15:00:00+01:00"

    @Nested
    inner class ToTimesheetEntries {

        private val responseBodyMapper = ResponseBodyMapper()

        @Test
        fun `should map to timesheet entries `() {
            val responseBody = ResponseBody(input())

            val timesheetEntries = responseBodyMapper.toTimesheetEntries(responseBody)

            assertThat(timesheetEntries).isEqualTo(expected())
        }
    }

    private fun input(): List<TimeEntry> = listOf(
        TimeEntry(
            aProject,
            aTask,
            someTagsAsTags,
            TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        TimeEntry(
            aProject,
            aTask,
            someTagsAsTags,
            TimeInterval(anotherDateLowerAsString, anotherDateUpperAsString, toSeconds(3))
        ),
        TimeEntry(
            aProject,
            anotherTask,
            someTagsAsTags,
            TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        TimeEntry(
            anotherProject,
            aTask,
            someTagsAsTags,
            TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
        TimeEntry(
            anotherProject,
            anotherTask,
            someTagsAsTags,
            TimeInterval(aDateLowerAsString, aDateUpperAsString, toSeconds(2))
        ),
    )

    private fun expected(): List<TimesheetEntry> = listOf(
        TimesheetEntry.of(
            aProject,
            aTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.toDuration(HOURS)
        ),
        TimesheetEntry.of(
            aProject,
            aTask,
            someTagsAsStrings,
            anotherDateLowerAsDateTime,
            anotherDateUpperAsDateTime,
            3.toDuration(HOURS)
        ),
        TimesheetEntry.of(
            aProject,
            anotherTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.toDuration(HOURS)
        ),
        TimesheetEntry.of(
            anotherProject,
            aTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.toDuration(HOURS)
        ),
        TimesheetEntry.of(
            anotherProject,
            anotherTask,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            2.toDuration(HOURS)
        )
    )

    private fun toSeconds(hours: Int) = (hours * DateTimeConstants.SECONDS_PER_HOUR).toLong()
}
