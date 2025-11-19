package dev.hertlein.timesheetwizard.core.export.domain.model

import dev.hertlein.timesheetwizard.core.export.domain.model.ExportTimesheet.Entry.DateTimeRange
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.aZoneOffset
import dev.hertlein.timesheetwizard.core.util.TestFixture.Export.anEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("Timesheet")
internal class ExportTimesheetTest {

    @Nested
    inner class TotalDuration {

        @Test
        fun `should sum up duration of all entries`() {
            val entries = (1..10).map {
                anEntry.copy(duration = it.hours)
            }
            val timesheet = aTimesheet(entries)

            val totalDuration = timesheet.totalDuration()

            assertThat(totalDuration).isEqualTo((55.hours))
        }

        @Test
        fun `should sum up duration if no entries present`() {
            val totalDuration = aTimesheet(emptyList()).totalDuration()

            assertThat(totalDuration).isEqualTo((0.hours))
        }
    }

    @Nested
    inner class GroupByProjectAndTaskAndTagsAndStartDate {

        @Test
        fun `should group the entries`() {
            val morningWorkDurationHours = 2L
            val afterNoonWorkDurationHours = 3L
            val morningWorkStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)
            val morningWorkEnd = morningWorkStart.plusHours(morningWorkDurationHours)
            val morningWork = anEntry.copy(
                duration = morningWorkDurationHours.toDuration(DurationUnit.HOURS),
                dateTimeRange = DateTimeRange(morningWorkStart, morningWorkEnd)
            )
            val afternoonWorkStart = morningWorkEnd.plusHours(1)
            val afternoonWorkEnd = afternoonWorkStart.plusHours(afterNoonWorkDurationHours)
            val afternoonWork = anEntry.copy(
                duration = afterNoonWorkDurationHours.toDuration(DurationUnit.HOURS),
                dateTimeRange = DateTimeRange(afternoonWorkStart, afternoonWorkEnd)
            )

            val actual = aTimesheet(morningWork, afternoonWork).entriesGroupedByProjectAndTaskAndTagsAndStartDate()

            assertThat(actual).isEqualTo(
                    listOf(anEntry.copy(
                        duration = (morningWorkDurationHours + afterNoonWorkDurationHours).toDuration(DurationUnit.HOURS),
                        dateTimeRange = DateTimeRange(morningWorkStart.truncatedTo(ChronoUnit.DAYS), morningWorkStart.truncatedTo(ChronoUnit.DAYS))
                    ))
            )
        }
    }
}
