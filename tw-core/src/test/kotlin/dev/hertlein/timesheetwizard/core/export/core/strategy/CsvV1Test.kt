package dev.hertlein.timesheetwizard.core.export.core.strategy

import dev.hertlein.timesheetwizard.core.ResourcesReader
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet
import dev.hertlein.timesheetwizard.core.shared.model.Timesheet.Entry.DateTimeRange
import dev.hertlein.timesheetwizard.core.util.TestFixture.aZoneOffset
import dev.hertlein.timesheetwizard.core.util.TestFixture.anEmptyTimesheet
import dev.hertlein.timesheetwizard.core.util.TestFixture.anEntry
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@DisplayName("CsvV1")
class CsvV1Test {

    @Test
    fun `should create a csv document from timesheet`() {
        val morningWorkDurationHours = 2L
        val lunchBreakDurationMinutes = 30L
        val afterNoonWorkDurationHours = 1L

        val morningWorkStart = OffsetDateTime.of(2022, 1, 1, 8, 0, 0, 0, aZoneOffset)
        val morningWorkEnd = morningWorkStart.plusHours(morningWorkDurationHours)
        val morningWork = anEntry.copy(
            duration = morningWorkDurationHours.toDuration(DurationUnit.HOURS),
            dateTimeRange = DateTimeRange(morningWorkStart, morningWorkEnd)
        )
        val lunchBreakStart = morningWorkEnd
        val lunchBreakEnd = morningWorkEnd.plusMinutes(lunchBreakDurationMinutes)
        val lunchBreak = anEntry.copy(
            task = Timesheet.Entry.Task(CsvV1.Companion.TASK_BREAK),
            duration = lunchBreakDurationMinutes.toDuration(DurationUnit.MINUTES),
            dateTimeRange = DateTimeRange(lunchBreakStart, lunchBreakEnd)
        )
        val afternoonWorkStart = lunchBreakEnd
        val afternoonWorkEnd = afternoonWorkStart.plusHours(afterNoonWorkDurationHours)
        val afternoonWork = anEntry.copy(
            duration = afterNoonWorkDurationHours.toDuration(DurationUnit.HOURS),
            dateTimeRange = DateTimeRange(afternoonWorkStart, afternoonWorkEnd)
        )

        val timesheet = anEmptyTimesheet.add(morningWork).add(lunchBreak).add(afternoonWork)
        val expected =
            ResourcesReader.bytesFromResourceFile("${this.javaClass.packageName}/timesheet_PiedPiper_20220101-20221231.csv")

        val actual = CsvV1().create(mapOf("login" to "rihe"), timesheet)

        assertThat(String(actual.content)).isEqualTo(String(expected))
    }
}