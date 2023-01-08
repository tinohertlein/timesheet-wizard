package dev.hertlein.timesheetwizard.importclockify.adapter.clockify.component

import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody.TimeEntry
import dev.hertlein.timesheetwizard.importclockify.adapter.clockify.model.ResponseBody.TimeEntry.TimeInterval
import dev.hertlein.timesheetwizard.importclockify.model.TimesheetEntry
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTimeConstants
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.LocalDate
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

    private val dateLowerAsDate = LocalDate.of(2022, 8, 1)
    private val dateLowerAsString = "2022-08-01T08:00:00+01:00"

    private val dateUpperAsDate = LocalDate.of(2022, 9, 30)
    private val dateUpperAsString = "2022-09-30T08:00:00+01:00"

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
        TimeEntry(aProject, aTask, someTagsAsTags, TimeInterval(dateLowerAsString, "", toSeconds(10))),
        TimeEntry(aProject, aTask, someTagsAsTags, TimeInterval(dateUpperAsString, "", toSeconds(1))),
        TimeEntry(aProject, anotherTask, someTagsAsTags, TimeInterval(dateLowerAsString, "", toSeconds(10))),
        TimeEntry(aProject, anotherTask, someTagsAsTags, TimeInterval(dateUpperAsString, "", toSeconds(1))),
        TimeEntry(anotherProject, aTask, someTagsAsTags, TimeInterval(dateLowerAsString, "", toSeconds(10))),
        TimeEntry(anotherProject, aTask, someTagsAsTags, TimeInterval(dateUpperAsString, "", toSeconds(1))),
        TimeEntry(anotherProject, anotherTask, someTagsAsTags, TimeInterval(dateLowerAsString, "", toSeconds(10))),
        TimeEntry(anotherProject, anotherTask, someTagsAsTags, TimeInterval(dateUpperAsString, "", toSeconds(1))),
    )

    private fun expected(): List<TimesheetEntry> = listOf(
        TimesheetEntry(aProject, aTask, someTagsAsStrings, dateLowerAsDate, 10.toDuration(HOURS)),
        TimesheetEntry(aProject, aTask, someTagsAsStrings, dateUpperAsDate, 1.toDuration(HOURS)),
        TimesheetEntry(aProject, anotherTask, someTagsAsStrings, dateLowerAsDate, 10.toDuration(HOURS)),
        TimesheetEntry(aProject, anotherTask, someTagsAsStrings, dateUpperAsDate, 1.toDuration(HOURS)),
        TimesheetEntry(anotherProject, aTask, someTagsAsStrings, dateLowerAsDate, 10.toDuration(HOURS)),
        TimesheetEntry(anotherProject, aTask, someTagsAsStrings, dateUpperAsDate, 1.toDuration(HOURS)),
        TimesheetEntry(anotherProject, anotherTask, someTagsAsStrings, dateLowerAsDate, 10.toDuration(HOURS)),
        TimesheetEntry(anotherProject, anotherTask, someTagsAsStrings, dateUpperAsDate, 1.toDuration(HOURS))
    )

    private fun toSeconds(hours: Int) = (hours * DateTimeConstants.SECONDS_PER_HOUR).toLong()
}
