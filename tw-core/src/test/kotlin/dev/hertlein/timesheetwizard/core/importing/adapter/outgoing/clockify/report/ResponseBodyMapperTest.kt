package dev.hertlein.timesheetwizard.core.importing.adapter.outgoing.clockify.report

import dev.hertlein.timesheetwizard.core.importing.domain.model.ImportTimesheet
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

    private val billable = true
    private val notBillable = false

    private val aProjectId = "#1"
    private val aProjectName = "a project"
    private val anotherProjectId = "#2"
    private val anotherProjectName = "another project"

    private val aTaskId = "#11"
    private val aTaskName = "a task"
    private val anotherTaskId = "#22"
    private val anotherTaskName = "another task"

    private val aDescription = "a task"
    private val anotherDescription = "another task"

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
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(1))
        ),
        ResponseBody.TimeEntry(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsTags,
            notBillable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(2))
        ),
        ResponseBody.TimeEntry(
            aProjectId,
            aProjectName,
            anotherTaskId,
            anotherTaskName,
            aDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(3))
        ),
        ResponseBody.TimeEntry(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(anotherDateLowerAsString, anotherDateUpperAsString, hoursToSeconds(4))
        ),
        ResponseBody.TimeEntry(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            anotherDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(5))
        ),
        ResponseBody.TimeEntry(
            anotherProjectId,
            anotherProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(6))
        ),
        ResponseBody.TimeEntry(
            anotherProjectId,
            anotherProjectName,
            aTaskId,
            aTaskName,
            anotherDescription,
            someTagsAsTags,
            billable,
            ResponseBody.TimeEntry.TimeInterval(aDateLowerAsString, aDateUpperAsString, hoursToSeconds(7))
        ),
    )

    private fun expected(): List<ImportTimesheet.Entry> = listOf(
        ImportTimesheet.Entry.of(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            billable,
            1.hours
        ),
        ImportTimesheet.Entry.of(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            notBillable,
            2.hours
        ),
        ImportTimesheet.Entry.of(
            aProjectId,
            aProjectName,
            anotherTaskId,
            anotherTaskName,
            aDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            billable,
            3.hours
        ),
        ImportTimesheet.Entry.of(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsStrings,
            anotherDateLowerAsDateTime,
            anotherDateUpperAsDateTime,
            billable,
            4.hours
        ),
        ImportTimesheet.Entry.of(
            aProjectId,
            aProjectName,
            aTaskId,
            aTaskName,
            anotherDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            billable,
            5.hours
        ),
        ImportTimesheet.Entry.of(
            anotherProjectId,
            anotherProjectName,
            aTaskId,
            aTaskName,
            aDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            billable,
            6.hours
        ),
        ImportTimesheet.Entry.of(
            anotherProjectId,
            anotherProjectName,
            aTaskId,
            aTaskName,
            anotherDescription,
            someTagsAsStrings,
            aDateLowerAsDateTime,
            aDateUpperAsDateTime,
            billable,
            7.hours
        )
    )

    private fun hoursToSeconds(hours: Int) = (hours * SECONDS_PER_HOUR).toLong()
}